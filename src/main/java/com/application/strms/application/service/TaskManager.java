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
    private final Map<Ulid, Task> allTasks = new HashMap<>();
    private final Set<Ulid> inProgressTaskIds = new HashSet<>();
    private final Set<Ulid> blockedTaskIds = new HashSet<>();
    private final PriorityQueue<Task> readyTasks = new PriorityQueue<>();

    public TaskManager(TaskRepository taskRepository) throws IOException {
        if (taskRepository == null) {
            throw new IllegalArgumentException("Task repository cannot be null");
        }

        this.taskRepository = taskRepository;
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
                    } catch (InsufficientPermissionsException e) {
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

            if (title != null && !title.isBlank()) {
                task.updateTitle(title, currentUser);
            }

            if (description != null) {
                task.updateDescription(description, currentUser);
            }

            if (priority != null) {
                task.changePriority(priority, currentUser);
                needsCollectionUpdate = true;
            }

            if (category != null) {
                task.updateCategory(category, currentUser);
            }

            if (deadline != null) {
                task.updateDeadline(deadline, currentUser);
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
            task.assignEngineer(engineer, currentUser);
            taskRepository.update(task);
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
            task.updateStatus(newStatus, currentUser);
            taskRepository.update(task);
            updateCollections(task);
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
            task.markAsDone(currentUser);
            taskRepository.update(task);
            updateCollections(task);
            return UpdateTaskResult.success();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return UpdateTaskResult.failure(e.getMessage());
        }
    }

    public List<Task> getReadyTasks() {
        return readyTasks.stream().toList();
    }

    public List<Task> getInProgressTasks() {
        return inProgressTaskIds.stream()
                .map(allTasks::get)
                .filter(task -> task != null && task.getStatus() == TaskStatus.IN_PROGRESS)
                .toList();
    }

    public List<Task> getBlockedTasks() {
        return blockedTaskIds.stream()
                .map(allTasks::get)
                .filter(task -> task != null && task.getStatus() == TaskStatus.BLOCKED)
                .toList();
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

    public void reloadTasks() throws IOException {
        allTasks.clear();
        inProgressTaskIds.clear();
        blockedTaskIds.clear();
        readyTasks.clear();
        loadTasks();
    }

    public void saveAllTasks() throws IOException {
        for (Task task : allTasks.values()) {
            try {
                taskRepository.update(task);
            } catch (IOException e) {
                throw new IOException("Failed to save task: " + task.getUlid(), e);
            }
        }
    }

    private Task findTaskOrThrow(Ulid taskId) {
        Task task = allTasks.get(taskId);

        if (task == null) {
            throw new TaskNotFoundException("Task not found: " + taskId);
        }

        return task;
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
            case TO_DO:
                readyTasks.offer(task);
                break;
            case DONE:
                break;
        }
    }

    private void loadTasks() throws IOException {
        List<Task> loadedTasks = taskRepository.findAll();

        for (Task task : loadedTasks) {
            allTasks.put(task.getUlid(), task);
        }

        for (Task task : allTasks.values()) {
            updateCollections(task);
        }
    }
}
