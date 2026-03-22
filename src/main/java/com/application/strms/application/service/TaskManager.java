package com.application.strms.application.service;

import com.application.strms.application.result.CreateTaskResult;
import com.application.strms.application.result.DeleteTaskResult;
import com.application.strms.application.result.UpdateTaskResult;
import com.application.strms.domain.exception.InsufficientPermissionsException;
import com.application.strms.domain.exception.TaskNotFoundException;
import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.PriorityLevel;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.TaskCategory;
import com.application.strms.domain.model.TaskHistoryEntry;
import com.application.strms.domain.model.TaskStatus;
import com.application.strms.domain.model.Ulid;
import com.application.strms.domain.model.User;
import com.application.strms.domain.repository.TaskRepository;
import com.application.strms.presentation.service.NotificationManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class TaskManager {
    private final TaskRepository taskRepository;
    private final NotificationManager notificationManager;
    private final Map<Ulid, Task> allTasks = new HashMap<>();
    private final Set<Ulid> inProgressTaskIds = new HashSet<>();
    private final Set<Ulid> blockedTaskIds = new HashSet<>();
    private final PriorityQueue<Task> readyTasks = new PriorityQueue<>();

    public TaskManager(TaskRepository taskRepository, NotificationManager notificationManager) throws IOException {
        if (taskRepository == null) {
            throw new IllegalArgumentException("Task repository cannot be null");
        }
        if (notificationManager == null) {
            throw new IllegalArgumentException("NotificationManager cannot be null");
        }

        this.taskRepository = taskRepository;
        this.notificationManager = notificationManager;
        loadTasks();
    }

    public CreateTaskResult createTask(String title, String description, PriorityLevel priority,
            TaskCategory category, LocalDateTime deadline, User currentUser) throws IOException {
        if (currentUser == null) {
            return CreateTaskResult.failure("Current user cannot be null");
        }

        try {
            Task task = new Task(title, description, priority, category, deadline);
            taskRepository.save(task);
            allTasks.put(task.getUlid(), task);
            updateCollections(task);
            return CreateTaskResult.success(task);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return CreateTaskResult.failure(e.getMessage());
        }
    }

    public DeleteTaskResult deleteTask(Ulid taskId, User currentUser) throws IOException {
        try {
            if (taskId == null) {
                return DeleteTaskResult.failure("Task ID cannot be null");
            }

            Task task = findTaskOrThrow(taskId);

            for (Task otherTask : allTasks.values()) {
                if (otherTask.getDependencies().contains(task)) {
                    try {
                        otherTask.removeDependency(task, currentUser);
                        updateCollections(otherTask);
                    } catch (InsufficientPermissionsException e) {
                        throw new IOException("Cannot remove dependency: " + e.getMessage(), e);
                    }
                }
            }

            taskRepository.delete(taskId);
            allTasks.remove(taskId);
            inProgressTaskIds.remove(taskId);
            blockedTaskIds.remove(taskId);
            readyTasks.removeIf(t -> t.getUlid().equals(taskId));
            return DeleteTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return DeleteTaskResult.failure(e.getMessage());
        }
    }

    public Task findTaskById(Ulid taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        return allTasks.get(taskId);
    }

    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(new ArrayList<>(allTasks.values()));
    }

    public UpdateTaskResult updateTask(Ulid taskId, String title, String description, PriorityLevel priority,
            TaskCategory category, LocalDateTime deadline, User currentUser) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);

            boolean needsCollectionUpdate = false;

            if (title != null && !title.isBlank() && !task.getTitle().equals(title)) {
                String oldTitle = task.getTitle();
                task.updateTitle(title, currentUser);
                addHistoryEntry(task, "Title changed", "title", oldTitle, title, currentUser);
            }

            if (description != null && !task.getDescription().equals(description)) {
                String oldDescription = task.getDescription();
                task.updateDescription(description, currentUser);
                addHistoryEntry(task, "Description changed", "description", oldDescription, description, currentUser);
            }

            if (priority != null && !task.getPriority().equals(priority)) {
                String oldPriority = task.getPriority().toString();
                String newPriority = priority.toString();
                task.changePriority(priority, currentUser);
                addHistoryEntry(task, "Priority changed", "priority", oldPriority, newPriority, currentUser);
                needsCollectionUpdate = true;
            }

            if (category != null && !task.getCategory().equals(category)) {
                String oldCategory = task.getCategory().toString();
                String newCategory = category.toString();
                task.updateCategory(category, currentUser);
                addHistoryEntry(task, "Category changed", "category", oldCategory, newCategory, currentUser);
            }

            if (deadline != null && !deadlinesEqual(task.getDeadline(), deadline)) {
                String oldDeadline = task.getDeadline() != null ? task.getDeadline().toString() : "null";
                String newDeadline = deadline.toString();
                task.updateDeadline(deadline, currentUser);
                addHistoryEntry(task, "Deadline changed", "deadline", oldDeadline, newDeadline, currentUser);
            }

            taskRepository.update(task);
            if (needsCollectionUpdate) {
                updateCollections(task);
            }
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public UpdateTaskResult assignTask(Ulid taskId, Engineer engineer, User currentUser) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);
            Engineer oldEngineer = task.getAssignedEngineer();
            String oldEngineerValue = oldEngineer != null ? oldEngineer.getName() : "null";
            String newEngineerValue = engineer != null ? engineer.getName() : "null";

            task.assignEngineer(engineer, currentUser);
            addHistoryEntry(task, "Task assigned", "assignedEngineer", oldEngineerValue, newEngineerValue, currentUser);
            taskRepository.update(task);
            if (engineer != null) {
                notificationManager.notifyAssignment(task, engineer);
            }
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public UpdateTaskResult unassignTask(Ulid taskId, User currentUser) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);
            task.unassignEngineer(currentUser);
            taskRepository.update(task);
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public UpdateTaskResult addDependency(Ulid taskId, Ulid dependencyId, User currentUser) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);
            Task dependency = findTaskOrThrow(dependencyId);

            task.addDependency(dependency, currentUser);
            addHistoryEntry(task, "Dependency added", "dependencies", "none", dependency.getTitle(), currentUser);
            taskRepository.update(task);
            updateCollections(task);
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public UpdateTaskResult removeDependency(Ulid taskId, Ulid dependencyId, User currentUser) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);
            Task dependency = findTaskOrThrow(dependencyId);
            task.removeDependency(dependency, currentUser);
            taskRepository.update(task);
            updateCollections(task);
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public UpdateTaskResult changeTaskStatus(Ulid taskId, TaskStatus newStatus, User currentUser) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);
            TaskStatus oldStatus = task.getStatus();

            task.updateStatus(newStatus, currentUser);
            addHistoryEntry(task, "Status changed", "status", oldStatus.toString(), newStatus.toString(), currentUser);
            taskRepository.update(task);
            updateCollections(task);
            notificationManager.notifyStatusChange(task, newStatus);
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public UpdateTaskResult markTaskAsDone(Ulid taskId, User currentUser) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);
            TaskStatus oldStatus = task.getStatus();

            task.markAsDone(currentUser);
            addHistoryEntry(task, "Task completed", "status", oldStatus.toString(), TaskStatus.DONE.toString(),
                    currentUser);
            taskRepository.update(task);
            updateCollections(task);
            notificationManager.notifyStatusChange(task, TaskStatus.DONE);
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public UpdateTaskResult addTaskHistoryEntry(Ulid taskId, TaskHistoryEntry entry) throws IOException {
        try {
            Task task = findTaskOrThrow(taskId);
            task.addHistoryEntry(entry);
            taskRepository.update(task);
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    private Task findTaskOrThrow(Ulid taskId) {
        Task task = allTasks.get(taskId);

        if (task == null) {
            throw new TaskNotFoundException("Task not found: " + taskId);
        }

        return task;
    }

    private void addHistoryEntry(Task task, String action, String fieldChanged, String oldValue, String newValue,
            User performedBy) {
        TaskHistoryEntry entry = new TaskHistoryEntry(action, performedBy, LocalDateTime.now(), fieldChanged, oldValue,
                newValue);
        task.addHistoryEntry(entry);
    }

    private boolean deadlinesEqual(LocalDateTime d1, LocalDateTime d2) {
        if (d1 == null && d2 == null)
            return true;
        if (d1 == null || d2 == null)
            return false;
        return d1.equals(d2);
    }

    private void updateCollections(Task task) {
        Ulid taskId = task.getUlid();

        inProgressTaskIds.remove(taskId);
        blockedTaskIds.remove(taskId);
        readyTasks.removeIf(t -> t.getUlid().equals(taskId));

        TaskStatus status = task.getStatus();
        switch (status) {
            case IN_PROGRESS:
                inProgressTaskIds.add(taskId);
                break;
            case BLOCKED:
                blockedTaskIds.add(taskId);
                break;
            case DONE:
                readyTasks.offer(task);
                break;
            case TO_DO:
                break;
        }
    }

    private void loadTasks() {
        List<Task> loadedTasks = taskRepository.findAll();

        for (Task task : loadedTasks) {
            allTasks.put(task.getUlid(), task);
        }

        refreshAllTaskStates();
    }

    public void refreshAllTaskStates() {
        inProgressTaskIds.clear();
        blockedTaskIds.clear();
        readyTasks.clear();

        for (Task task : allTasks.values()) {
            task.refreshStatusFromDependencies();
            updateCollections(task);
        }
    }

    public List<Task> getReadyTasks() {
        return new ArrayList<>(readyTasks);
    }

    public List<Task> getInProgressTasks() {
        List<Task> inProgressTasks = new ArrayList<>();
        for (Ulid taskId : inProgressTaskIds) {
            Task task = allTasks.get(taskId);
            if (task != null) {
                inProgressTasks.add(task);
            }
        }
        return inProgressTasks;
    }

    public List<Task> getBlockedTasks() {
        List<Task> blockedTasks = new ArrayList<>();
        for (Ulid taskId : blockedTaskIds) {
            Task task = allTasks.get(taskId);
            if (task != null) {
                blockedTasks.add(task);
            }
        }
        return blockedTasks;
    }

    public List<Task> getTasksForEngineer(Engineer engineer) {
        if (engineer == null) {
            throw new IllegalArgumentException("Engineer cannot be null");
        }

        List<Task> engineerTasks = new ArrayList<>();
        for (Task task : allTasks.values()) {
            Engineer assigned = task.getAssignedEngineer();
            if (assigned != null && assigned.getId().equals(engineer.getId())) {
                engineerTasks.add(task);
            }
        }
        return engineerTasks;
    }
}
