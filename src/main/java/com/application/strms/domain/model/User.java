package com.application.strms.domain.model;

public class User {

    private final UserId id;
    private final String name;
    private final Email email;
    private final String passwordHash;

    public User(UserId id, String name, Email email, String passwordHash) {
        if (id == null) throw new IllegalArgumentException("Id cannot be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be empty");
        if (email == null) throw new IllegalArgumentException("Email cannot be null");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("Password hash cannot be empty");

        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UserId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }
}