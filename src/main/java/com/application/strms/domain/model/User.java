package com.application.strms.domain.model;

public class User {
    private final UserId id;
    private final String name;
    private final Email email;
    private final String role;

    public User(UserId id, String name, Email email, String role) {
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
        this(new UserId(), name, email, role);
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

    public String role() {
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