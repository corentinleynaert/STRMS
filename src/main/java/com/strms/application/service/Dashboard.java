package com.strms.application.service;

import com.strms.domain.model.Engineer;
import com.strms.domain.model.TaskStatus;
import com.strms.domain.repository.UserRepository;

import java.time.LocalDateTime;

public class Dashboard {
    private final TaskManager taskManager;
    private final UserRepository userRepository;

    public Dashboard(TaskManager taskManager, UserRepository userRepository) {
        if (taskManager == null) {
            throw new IllegalArgumentException("TaskManager cannot be null");
        }
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null");
        }
        this.taskManager = taskManager;
        this.userRepository = userRepository;
    }

    public int getTotalTasks() {
        return taskManager.getAllTasks().size();
    }

    public int getTodoTasks() {
        return (int) taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.TO_DO)
                .count();
    }

    public int getInProgressTasks() {
        return (int) taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
                .count();
    }

    public int getBlockedTasks() {
        return (int) taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.BLOCKED)
                .count();
    }

    public int getDoneTasks() {
        return (int) taskManager.getAllTasks().stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
    }

    public int getTotalUsers() {
        return userRepository.getAllUsers().size();
    }

    public int getEngineersCount() {
        return (int) userRepository.getAllUsers().stream()
                .filter(user -> user instanceof Engineer)
                .count();
    }

    public int getOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        return (int) taskManager.getAllTasks().stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();
    }
}
