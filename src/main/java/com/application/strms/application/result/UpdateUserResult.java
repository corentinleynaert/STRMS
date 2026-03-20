package com.application.strms.application.result;

import com.application.strms.domain.model.User;

public class UpdateUserResult {
    private final boolean success;
    private final String error;
    private final User user;

    private UpdateUserResult(boolean success, String error, User user) {
        this.success = success;
        this.error = error;
        this.user = user;
    }

    public static UpdateUserResult success(User user) {
        return new UpdateUserResult(true, null, user);
    }

    public static UpdateUserResult failure(String error) {
        return new UpdateUserResult(false, error, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return success ? "Success" : error;
    }
}