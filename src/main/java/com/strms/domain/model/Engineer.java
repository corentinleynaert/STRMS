package com.strms.domain.model;

public class Engineer extends User {
    public Engineer(Ulid id, String name, Email email) {
        super(id, name, email);
    }

    public Engineer(String name, Email email) {
        super(name, email);
    }

    @Override
    public UserRole getRole() {
        return UserRoleFactory.createEngineer();
    }
}