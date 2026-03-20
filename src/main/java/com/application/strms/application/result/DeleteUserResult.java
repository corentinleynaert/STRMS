package com.application.strms.application.result;

public class DeleteUserResult {
    private final boolean success;
    private final String error;

    private DeleteUserResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public static DeleteUserResult success() {
        return new DeleteUserResult(true, null);
    }

    public static DeleteUserResult failure(String error) {
        return new DeleteUserResult(false, error);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }
}
