package com.application.strms.domain.model;

public class Manager extends User {
    public Manager(UserId id, String name, Email email) {
        super(id, name, email, "MANAGER");
    }
    public Manager(String name, Email email) {
        super(name, email, "MANAGER");
    }
}