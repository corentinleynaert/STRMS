package com.strms.application.result;

import com.strms.domain.model.Task;

public class CreateTaskResult {
    private final boolean success;
    private final String message;
    private final Task task;

    private CreateTaskResult(boolean success, String message, Task task) {
        this.success = success;
        this.message = message;
        this.task = task;
    }

    public static CreateTaskResult success(Task task) {
        return new CreateTaskResult(true, null, task);
    }

    public static CreateTaskResult failure(String message) {
        return new CreateTaskResult(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Task getTask() {
        return task;
    }
}
