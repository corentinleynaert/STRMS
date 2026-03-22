package com.application.strms.presentation.service;

import com.application.strms.application.service.TaskManager;
import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Dashboard {

    @SuppressWarnings("unused")
    public void displayTasksByUser(TaskManager taskManager) {
        System.out.println("\n===== TASKS BY USER =====\n");

        List<Task> allTasks = taskManager.getReadyTasks();
        allTasks.addAll(taskManager.getInProgressTasks());
        allTasks.addAll(taskManager.getBlockedTasks());

        Map<String, List<Task>> tasksByEngineer = allTasks.stream()
                .filter(task -> task.getAssignedEngineer() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getAssignedEngineer().getName(),
                        Collectors.toList()));

        if (tasksByEngineer.isEmpty()) {
            System.out.println("No tasks assigned to engineers.\n");
        } else {
            tasksByEngineer.forEach((engineerName, tasks) -> {
                System.out.println("- " + engineerName + ": " + tasks.size() + " tasks");
                tasks.forEach(task -> System.out.println("  • " + task.getTitle() + " (" + task.getStatus() + ")"));
            });
        }

        System.out.println("\n== Unassigned Tasks ==");
        long unassignedCount = allTasks.stream()
                .filter(task -> task.getAssignedEngineer() == null)
                .count();
        System.out.println("Unassigned: " + unassignedCount + " tasks\n");
    }

    @SuppressWarnings("unused")
    public void displayTasksByStatus(TaskManager taskManager) {
        System.out.println("\n===== TASKS BY STATUS =====\n");

        Map<TaskStatus, List<Task>> tasksByStatus = Map.of(
                TaskStatus.TO_DO, taskManager.getReadyTasks(),
                TaskStatus.IN_PROGRESS, taskManager.getInProgressTasks(),
                TaskStatus.BLOCKED, taskManager.getBlockedTasks(),
                TaskStatus.DONE, List.of());

        for (TaskStatus status : TaskStatus.values()) {
            List<Task> tasks = tasksByStatus.getOrDefault(status, List.of());
            System.out.println("- " + status + ": " + tasks.size());
        }

        System.out.println();
    }

    @SuppressWarnings("unused")
    public void displayOverdueTasks(TaskManager taskManager) {
        System.out.println("\n===== OVERDUE TASKS =====\n");

        List<Task> allTasks = taskManager.getReadyTasks();
        allTasks.addAll(taskManager.getInProgressTasks());
        allTasks.addAll(taskManager.getBlockedTasks());

        LocalDateTime now = LocalDateTime.now();

        List<Task> overdueTasks = allTasks.stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();

        if (overdueTasks.isEmpty()) {
            System.out.println("✓ No overdue tasks!\n");
        } else {
            System.out.println("⚠ " + overdueTasks.size() + " overdue task(s):\n");
            overdueTasks.forEach(task -> {
                System.out.println("- " + task.getTitle());
                System.out.println("  Deadline: " + task.getDeadline());
                System.out.println("  Status: " + task.getStatus());
                if (task.getAssignedEngineer() != null) {
                    System.out.println("  Assigned to: " + task.getAssignedEngineer().getName());
                }
                System.out.println();
            });
        }
    }

    @SuppressWarnings("unused")
    public void displayStatistics(TaskManager taskManager, UserRepository userRepository) {
        System.out.println("\n===== DASHBOARD =====\n");

        List<Task> allTasks = taskManager.getReadyTasks();
        allTasks.addAll(taskManager.getInProgressTasks());
        allTasks.addAll(taskManager.getBlockedTasks());

        System.out.println("Total Tasks: " + allTasks.size() + "\n");

        System.out.println("Tasks by Status:");
        Map<TaskStatus, List<Task>> tasksByStatus = Map.of(
                TaskStatus.TO_DO, taskManager.getReadyTasks(),
                TaskStatus.IN_PROGRESS, taskManager.getInProgressTasks(),
                TaskStatus.BLOCKED, taskManager.getBlockedTasks(),
                TaskStatus.DONE, List.of());

        for (TaskStatus status : TaskStatus.values()) {
            List<Task> tasks = tasksByStatus.getOrDefault(status, List.of());
            System.out.println("- " + status + ": " + tasks.size());
        }

        System.out.println("\nTasks by User:");
        Map<String, Long> tasksByUser = allTasks.stream()
                .filter(task -> task.getAssignedEngineer() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getAssignedEngineer().getName(),
                        Collectors.counting()));

        if (tasksByUser.isEmpty()) {
            System.out.println("- No assigned tasks");
        } else {
            tasksByUser.forEach((userName, count) -> System.out.println("- " + userName + ": " + count));
        }

        System.out.println("\nOverdue Tasks:");
        LocalDateTime now = LocalDateTime.now();

        List<Task> overdueTasks = allTasks.stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();

        if (overdueTasks.isEmpty()) {
            System.out.println("- ✓ No overdue tasks");
        } else {
            System.out.println("- ⚠ " + overdueTasks.size() + " overdue");
            overdueTasks.forEach(task -> System.out.println("  • " + task.getTitle()));
        }

        System.out.println("\nBlocked Tasks:");
        List<Task> blockedTasks = taskManager.getBlockedTasks();
        System.out.println("- Total: " + blockedTasks.size());

        if (!blockedTasks.isEmpty()) {
            blockedTasks.stream().limit(3).forEach(task -> System.out.println("  • " + task.getTitle()));
            if (blockedTasks.size() > 3) {
                System.out.println("  ... and " + (blockedTasks.size() - 3) + " more");
            }
        }

        System.out.println("\nUsers:");
        List<User> allUsers = userRepository.getAllUsers();
        long engineerCount = allUsers.stream()
                .filter(u -> u instanceof Engineer)
                .count();

        System.out.println("- Total Users: " + allUsers.size());
        System.out.println("- Engineers: " + engineerCount);

        System.out.println("\n===================\n");
    }
}