package com.application.strms.domain.model;

public class User {
    private final Ulid id;
    private final String name;
    private final Email email;
    private final String role;

    public User(Ulid id, String name, Email email, String role) {
        if (id == null) { throw new IllegalArgumentException("Id cannot be null"); }
        if (name == null || name.isBlank()) { throw new IllegalArgumentException("Name cannot be empty"); }
        if (email == null) { throw new IllegalArgumentException("Email cannot be null"); }
        if (role == null || role.isBlank()) { throw new IllegalArgumentException("Role cannot be empty"); }

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public User(String name, Email email, String role) {
        this(new Ulid(), name, email, role);
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

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return this instanceof Admin;
    }

    public boolean isEngineer() {
        return this instanceof Engineer;
    }

    public boolean isManager() {
        return this instanceof Manager;
    }
}