package com.strms.domain.model;

public class Admin extends User {
    public Admin(Ulid id, String name, Email email) {
        super(id, name, email);
    }

    public Admin(String name, Email email) {
        super(name, email);
    }

    @Override
    public UserRole getRole() {
        return UserRoleFactory.createAdmin();
    }
}