package com.application.strms.domain.model;

import com.application.strms.domain.exception.CircularDependencyException;
import com.application.strms.domain.exception.DependencyNotCompletedException;
import com.application.strms.domain.exception.InsufficientPermissionsException;
import com.application.strms.domain.exception.InvalidTaskStateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task implements Comparable<Task> {
    private final Ulid ulid;
    private String title;
    private String description;

    private PriorityLevel priority;
    private TaskStatus status;
    private TaskCategory category;

    private Engineer assignedEngineer;
    private LocalDateTime deadline;

    private final List<Task> dependencies;
    private final List<TaskHistoryEntry> history;

    public Task(String title,
            String description,
            PriorityLevel priority,
            TaskCategory category,
            LocalDateTime deadline) {

        this(new Ulid(), title, description, priority, category, TaskStatus.TO_DO, deadline);
    }

    public Task(Ulid ulid,
            String title,
            String description,
            PriorityLevel priority,
            TaskCategory category,
            TaskStatus status,
            LocalDateTime deadline) {

        if (ulid == null)
            throw new IllegalArgumentException("Ulid cannot be null");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be empty");
        if (description == null)
            throw new IllegalArgumentException("Description cannot be null");
        if (priority == null)
            throw new IllegalArgumentException("Priority cannot be null");
        if (category == null)
            throw new IllegalArgumentException("Category cannot be null");
        if (status == null)
            throw new IllegalArgumentException("Status cannot be null");

        this.ulid = ulid;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.category = category;
        this.assignedEngineer = null;
        this.deadline = deadline;

        this.dependencies = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public Ulid getUlid() {
        return ulid;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public Engineer getAssignedEngineer() {
        return assignedEngineer;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public List<Task> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public List<TaskHistoryEntry> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void updateTitle(String title, User actor) {
        validatePermission(actor, "canUpdateTask");
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be empty");
        this.title = title;
    }

    public void updateDescription(String description, User actor) {
        validatePermission(actor, "canUpdateTask");
        if (description == null)
            throw new IllegalArgumentException("Description cannot be null");
        this.description = description;
    }

    public void changePriority(PriorityLevel priority, User actor) {
        validatePermission(actor, "canUpdateTask");
        if (priority == null)
            throw new IllegalArgumentException("Priority cannot be null");
        this.priority = priority;
    }

    public void updateCategory(TaskCategory category, User actor) {
        validatePermission(actor, "canUpdateTask");
        if (category == null)
            throw new IllegalArgumentException("Category cannot be null");
        this.category = category;
    }

    public void updateDeadline(LocalDateTime deadline, User actor) {
        validatePermission(actor, "canUpdateTask");
        this.deadline = deadline;
    }

    public void assignEngineer(Engineer engineer, User actor) {
        validatePermission(actor, "canAssignTask");
        if (engineer == null)
            throw new IllegalArgumentException("Engineer cannot be null");
        this.assignedEngineer = engineer;
    }

    public void unassignEngineer(User actor) {
        validatePermission(actor, "canAssignTask");
        this.assignedEngineer = null;
    }

    public void addDependency(Task dependency, User actor) {
        validatePermission(actor, "canUpdateTask");
        if (dependency == null)
            throw new IllegalArgumentException("Dependency cannot be null");
        if (dependency == this)
            throw new IllegalArgumentException("A task cannot depend on itself");
        if (dependencies.contains(dependency))
            throw new IllegalArgumentException("Dependency already exists");
        if (dependency.dependsOn(this))
            throw new CircularDependencyException("Circular dependency detected");

        dependencies.add(dependency);
        refreshStatusFromDependencies();
    }

    public void addDependencyUnchecked(Task dependency) {
        if (dependency != null && !dependencies.contains(dependency)) {
            dependencies.add(dependency);
        }
    }

    public void assignEngineerUnchecked(Engineer engineer) {
        this.assignedEngineer = engineer;
    }

    public void removeDependency(Task dependency, User actor) {
        validatePermission(actor, "canUpdateTask");
        if (dependency == null)
            throw new IllegalArgumentException("Dependency cannot be null");
        if (!dependencies.contains(dependency))
            throw new IllegalArgumentException("Dependency does not exist");

        dependencies.remove(dependency);
        refreshStatusFromDependencies();
    }

    public void updateStatus(TaskStatus newStatus, User actor) {
        validatePermission(actor, "canChangeTaskStatus");
        if (newStatus == null)
            throw new IllegalArgumentException("Status cannot be null");

        if (status == TaskStatus.DONE && newStatus != TaskStatus.DONE) {
            throw new InvalidTaskStateException("Cannot revert a completed task from DONE to another state");
        }

        if (newStatus == TaskStatus.IN_PROGRESS && !areDependenciesCompleted()) {
            throw new DependencyNotCompletedException("Cannot transition to IN_PROGRESS: dependencies not completed");
        }

        this.status = newStatus;
    }

    public void markAsDone(User actor) {
        validatePermission(actor, "canChangeTaskStatus");
        if (!areDependenciesCompleted()) {
            throw new IllegalArgumentException("Dependencies not completed");
        }
        this.status = TaskStatus.DONE;
    }

    public void addHistoryEntry(TaskHistoryEntry entry) {
        if (entry == null)
            throw new IllegalArgumentException("History entry cannot be null");
        history.add(entry);
    }

    public void refreshStatusFromDependencies() {
        if (status == TaskStatus.DONE)
            return;

        if (dependencies.isEmpty() || areDependenciesCompleted()) {
            status = TaskStatus.TO_DO;
        } else {
            status = TaskStatus.BLOCKED;
        }
    }

    private boolean areDependenciesCompleted() {
        for (Task dependency : dependencies) {
            if (dependency.getStatus() != TaskStatus.DONE) {
                return false;
            }
        }
        return true;
    }

    private boolean dependsOn(Task target) {
        if (dependencies.contains(target))
            return true;

        for (Task dependency : dependencies) {
            if (dependency.dependsOn(target))
                return true;
        }
        return false;
    }

    private void validatePermission(User actor, String permissionType) {
        if (actor == null) {
            throw new InsufficientPermissionsException("Actor cannot be null");
        }

        UserRole role = actor.getRole();

        boolean hasPermission = switch (permissionType) {
            case "canUpdateTask" -> role.canUpdateTask();
            case "canChangeTaskStatus" -> role.canChangeTaskStatus();
            case "canAssignTask" -> role.canAssignTask();
            default -> throw new IllegalArgumentException("Unknown permission type: " + permissionType);
        };

        if (!hasPermission) {
            throw new InsufficientPermissionsException(
                    "User does not have permission to perform this action");
        }

        if (actor instanceof Engineer engineer && !permissionType.equals("canAssignTask")) {
            if (assignedEngineer != null && !assignedEngineer.getId().equals(engineer.getId())) {
                throw new InsufficientPermissionsException(
                        "Engineer can only modify tasks assigned to them");
            }
        }
    }

    @Override
    public int compareTo(Task other) {
        return other.priority.ordinal() - this.priority.ordinal();
    }

    @Override
    public String toString() {
        return title + " (" + priority + ") - " + status;
    }
}