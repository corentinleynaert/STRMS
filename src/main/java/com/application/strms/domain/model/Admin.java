package com.application.strms.domain.model;

public class Admin extends User {
    public Admin(UserId id, String name, Email email, String passwordHash) {
        super(id, name, email, passwordHash);
    }
}