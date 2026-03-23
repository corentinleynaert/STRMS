package com.strms.application.result;

public class DeleteTaskResult {
    private final boolean success;
    private final String message;

    private DeleteTaskResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static DeleteTaskResult success() {
        return new DeleteTaskResult(true, null);
    }

    public static DeleteTaskResult failure(String message) {
        return new DeleteTaskResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
