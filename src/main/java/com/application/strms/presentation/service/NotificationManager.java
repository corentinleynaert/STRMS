package com.application.strms.presentation.service;

import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.NotificationType;
import com.application.strms.domain.model.Task;
import com.application.strms.domain.model.TaskStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationManager {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void notify(Task task, NotificationType type, String message) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("NotificationType cannot be null");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }

        switch (type) {
            case CONSOLE:
                notifyConsole(task, message);
                break;
            case EMAIL:
                notifyEmail(task, message);
                break;
            case SMS:
                notifySms(task, message);
                break;
            default:
                notifyConsole(task, message);
                break;
        }
    }

    public void notifyAssignment(Task task, Engineer engineer) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (engineer == null) {
            throw new IllegalArgumentException("Engineer cannot be null");
        }

        String message = String.format(
                "Task \"%s\" (ID: %s) has been assigned to engineer %s",
                task.getTitle(),
                task.getUlid().toString(),
                engineer.getName());

        notify(task, NotificationType.CONSOLE, message);
    }

    public void notifyStatusChange(Task task, TaskStatus newStatus) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("TaskStatus cannot be null");
        }

        String message = String.format(
                "Task \"%s\" (ID: %s) status changed to %s",
                task.getTitle(),
                task.getUlid(),
                newStatus);

        notify(task, NotificationType.CONSOLE, message);
    }

    public void notifyDeadline(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (task.getDeadline() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = task.getDeadline();

        String message;
        if (now.isAfter(deadline)) {
            message = String.format(
                    "Task \"%s\" (ID: %s) is OVERDUE (deadline: %s)",
                    task.getTitle(),
                    task.getUlid().toString(),
                    deadline.format(DATE_FORMATTER));
        } else if (now.plusDays(1).isAfter(deadline)) {
            message = String.format(
                    "Task \"%s\" (ID: %s) is approaching its deadline (deadline: %s)",
                    task.getTitle(),
                    task.getUlid().toString(),
                    deadline.format(DATE_FORMATTER));
        } else {
            message = String.format(
                    "Task \"%s\" (ID: %s) reminder (deadline: %s)",
                    task.getTitle(),
                    task.getUlid().toString(),
                    deadline.format(DATE_FORMATTER));
        }

        notify(task, NotificationType.CONSOLE, message);
    }

    private void notifyConsole(Task task, String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        System.out.println("[NOTIFICATION][CONSOLE] [" + timestamp + "] " + message);
    }

    private void notifyEmail(Task task, String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        System.out.println("[NOTIFICATION][EMAIL] [" + timestamp + "] EMAIL notification for Task \"" + task.getTitle()
                + "\": " + message);
    }

    private void notifySms(Task task, String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        System.out.println("[NOTIFICATION][SMS] [" + timestamp + "] SMS notification for Task \"" + task.getTitle()
                + "\": " + message);
    }
}