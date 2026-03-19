package com.application.strms.application.result;

import com.application.strms.domain.model.User;

public class AddUserResult {
    private final boolean success;
    private final String error;
    private final User createdUser;

    private AddUserResult(boolean success, String error, User createdUser) {
        this.success = success;
        this.error = error;
        this.createdUser = createdUser;
    }

    public static AddUserResult success(User user) {
        return new AddUserResult(true, null, user);
    }

    public static AddUserResult failure(String error) {
        return new AddUserResult(false, error, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String error() {
        return error;
    }

    public User createdUser() {
        return createdUser;
    }
}