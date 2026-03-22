package com.application.strms.application.service;

import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {
    public String generateTaskReport(TaskManager taskManager) {
        StringBuilder report = new StringBuilder();
        report.append("===== TASK REPORT =====\n\n");

        List<Task> allTasks = taskManager.getAllTasks();

        report.append("Total Tasks: ").append(allTasks.size()).append("\n\n");

        Map<TaskStatus, Long> tasksByStatus = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        report.append("Tasks by Status:\n");
        for (TaskStatus status : TaskStatus.values()) {
            long count = tasksByStatus.getOrDefault(status, 0L);
            report.append("- ").append(status).append(": ").append(count).append("\n");
        }

        report.append("\nTasks by Priority:\n");
        Map<PriorityLevel, Long> tasksByPriority = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        for (PriorityLevel priority : PriorityLevel.values()) {
            long count = tasksByPriority.getOrDefault(priority, 0L);
            report.append("- ").append(priority).append(": ").append(count).append("\n");
        }

        report.append("\n=====================\n");
        return report.toString();
    }

    public String generateUserReport(UserRepository userRepository) {
        StringBuilder report = new StringBuilder();
        report.append("===== USER REPORT =====\n\n");

        List<User> allUsers = userRepository.getAllUsers();
        report.append("Total Users: ").append(allUsers.size()).append("\n\n");

        List<Engineer> engineers = allUsers.stream()
                .filter(u -> u instanceof Engineer)
                .map(u -> (Engineer) u)
                .toList();

        report.append("Engineers: ").append(engineers.size()).append("\n");
        for (Engineer engineer : engineers) {
            report.append("- ").append(engineer.getName()).append("\n");
        }

        report.append("\n=======================\n");
        return report.toString();
    }

    public String generateOverdueTasksReport(TaskManager taskManager) {
        StringBuilder report = new StringBuilder();
        report.append("===== OVERDUE TASKS REPORT =====\n\n");

        List<Task> allTasks = taskManager.getAllTasks();

        LocalDateTime now = LocalDateTime.now();

        List<Task> overdueTasks = allTasks.stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .toList();

        report.append("Total Overdue Tasks: ").append(overdueTasks.size()).append("\n\n");

        if (overdueTasks.isEmpty()) {
            report.append("No overdue tasks!\n");
        } else {
            report.append("Overdue Tasks:\n");
            overdueTasks.forEach(task -> {
                report.append("- ").append(task.getTitle())
                        .append(" (Deadline: ").append(task.getDeadline())
                        .append(", Status: ").append(task.getStatus()).append(")\n");
                if (task.getAssignedEngineer() != null) {
                    report.append("  Assigned to: ").append(task.getAssignedEngineer().getName()).append("\n");
                }
            });
        }

        report.append("\n================================\n");
        return report.toString();
    }

    public String generateTasksByPriorityReport(TaskManager taskManager) {
        StringBuilder report = new StringBuilder();
        report.append("===== TASKS BY PRIORITY =====\n\n");

        List<Task> allTasks = taskManager.getAllTasks();

        Map<PriorityLevel, List<Task>> tasksByPriority = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority));

        for (PriorityLevel priority : PriorityLevel.values()) {
            List<Task> tasksForPriority = tasksByPriority.getOrDefault(priority, List.of());
            report.append(priority).append(": ").append(tasksForPriority.size()).append(" tasks\n");

            tasksForPriority.forEach(task -> report.append("  - ").append(task.getTitle())
                    .append(" (").append(task.getStatus()).append(")\n"));

            report.append("\n");
        }

        report.append("=============================\n");
        return report.toString();
    }

    public String generateTasksByStatusReport(TaskManager taskManager) {
        StringBuilder report = new StringBuilder();
        report.append("===== TASKS BY STATUS =====\n\n");

        List<Task> allTasks = taskManager.getAllTasks();

        Map<TaskStatus, List<Task>> tasksByStatus = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));

        for (TaskStatus status : TaskStatus.values()) {
            List<Task> tasksForStatus = tasksByStatus.getOrDefault(status, List.of());
            report.append(status).append(": ").append(tasksForStatus.size()).append(" tasks\n");

            tasksForStatus.forEach(task -> {
                report.append("  - ").append(task.getTitle());
                if (task.getAssignedEngineer() != null) {
                    report.append(" (assigned to ").append(task.getAssignedEngineer().getName()).append(")");
                }
                report.append("\n");
            });

            report.append("\n");
        }

        report.append("===========================\n");
        return report.toString();
    }

    public String generateComprehensiveReport(TaskManager taskManager, UserRepository userRepository) {
        StringBuilder report = new StringBuilder();
        report.append("===== COMPREHENSIVE REPORT =====\n\n");

        List<Task> allTasks = taskManager.getAllTasks();

        List<User> allUsers = userRepository.getAllUsers();

        report.append("SUMMARY\n");
        report.append("Total Tasks: ").append(allTasks.size()).append("\n");
        report.append("Total Users: ").append(allUsers.size()).append("\n");
        report.append("Engineers: ").append(allUsers.stream().filter(u -> u instanceof Engineer).count())
                .append("\n\n");

        report.append("TASK STATUS BREAKDOWN\n");
        long todoCount = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.TO_DO).count();
        long inProgressCount = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long blockedCount = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.BLOCKED).count();
        long doneCount = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();

        report.append("- TO_DO: ").append(todoCount).append("\n");
        report.append("- IN_PROGRESS: ").append(inProgressCount).append("\n");
        report.append("- BLOCKED: ").append(blockedCount).append("\n");
        report.append("- DONE: ").append(doneCount).append("\n");

        LocalDateTime now = LocalDateTime.now();
        long overdueCount = allTasks.stream()
                .filter(task -> task.getDeadline() != null && task.getDeadline().isBefore(now))
                .filter(task -> task.getStatus() != TaskStatus.DONE)
                .count();
        report.append("- OVERDUE: ").append(overdueCount).append("\n\n");

        report.append("PRIORITY DISTRIBUTION\n");
        Map<PriorityLevel, Long> byPriority = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));
        for (PriorityLevel priority : PriorityLevel.values()) {
            report.append("- ").append(priority).append(": ").append(byPriority.getOrDefault(priority, 0L))
                    .append("\n");
        }

        report.append("\n================================\n");
        return report.toString();
    }
}
