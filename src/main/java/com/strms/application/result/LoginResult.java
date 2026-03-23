package com.strms.application.result;

import com.strms.domain.model.User;

public class LoginResult {
    private final boolean success;
    private final String error;
    private final User user;

    private LoginResult(boolean success, String error, User user) {
        this.success = success;
        this.error = error;
        this.user = user;
    }

    public static LoginResult success(User user) {
        return new LoginResult(true, null, user);
    }

    public static LoginResult failure(String error) {
        return new LoginResult(false, error, null);
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return error;
    }

    public User user() {
        return user;
    }
}