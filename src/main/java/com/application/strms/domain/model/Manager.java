package com.application.strms.domain.model;

public class Manager extends User {
    public Manager(UserId id, String name, Email email, String passwordHash) {
        super(id, name, email, passwordHash);
    }

    public Manager(String name, Email email, String passwordHash) {
        super(name, email, passwordHash);
    }
}