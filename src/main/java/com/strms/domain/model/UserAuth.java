package com.strms.domain.model;

public class UserAuth {
    private final Ulid id;
    private final String passwordHash;

    public UserAuth(Ulid id, String passwordHash) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }

        this.id = id;
        this.passwordHash = passwordHash;
    }

    public Ulid getId() {
        return id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
