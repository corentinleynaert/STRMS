package com.application.strms.domain.model;

public abstract class User {
    private final Ulid id;
    private final String name;
    private final Email email;

    protected User(Ulid id, String name, Email email) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }

        this.id = id;
        this.name = name;
        this.email = email;
    }

    protected User(String name, Email email) {
        this(new Ulid(), name, email);
    }

    public Ulid getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public abstract UserRole getRole();
}