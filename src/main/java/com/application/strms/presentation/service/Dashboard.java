package com.application.strms.presentation.service;

import com.application.strms.application.service.TaskManager;
import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.TaskStatus;
import com.application.strms.domain.repository.UserRepository;

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
}
