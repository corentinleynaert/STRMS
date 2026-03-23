package com.strms.domain.model;

public class Manager extends User {
    public Manager(Ulid id, String name, Email email) {
        super(id, name, email);
    }

    public Manager(String name, Email email) {
        super(name, email);
    }

    @Override
    public UserRole getRole() {
        return UserRoleFactory.createManager();
    }
}