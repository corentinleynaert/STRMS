package com.strms.application.service;

import com.strms.domain.model.Engineer;
import com.strms.domain.model.Task;
import com.strms.domain.model.TaskStatus;

public interface NotificationService {
    void notify(Task task, String message);

    void notifyAssignment(Task task, Engineer engineer);

    void notifyStatusChange(Task task, TaskStatus newStatus);

    void notifyDeadline(Task task);
}
