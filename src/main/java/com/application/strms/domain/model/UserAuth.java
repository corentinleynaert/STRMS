package com.application.strms.domain.model;

public class UserAuth {
    private final UserId id;
    private final String passwordHash;

    public UserAuth(UserId id, String passwordHash) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }

        this.id = id;
        this.passwordHash = passwordHash;
    }

    public UserId getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
