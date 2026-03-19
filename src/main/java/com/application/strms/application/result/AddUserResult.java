package com.application.strms.application.result;

public class AddUserResult {
    private final boolean success;
    private final String error;

    private AddUserResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public static AddUserResult success() {
        return new AddUserResult(true, null);
    }

    public static AddUserResult failure(String error) {
        return new AddUserResult(false, error);
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return error;
    }
}