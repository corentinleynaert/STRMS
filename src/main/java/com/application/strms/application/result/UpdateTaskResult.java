package com.application.strms.application.result;

public class UpdateTaskResult {
    private final boolean success;
    private final String message;

    private UpdateTaskResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static UpdateTaskResult success() {
        return new UpdateTaskResult(true, null);
    }

    public static UpdateTaskResult failure(String message) {
        return new UpdateTaskResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
