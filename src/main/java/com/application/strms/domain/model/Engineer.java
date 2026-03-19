package com.application.strms.domain.model;

public class Engineer extends User {
    public Engineer(UserId id, String name, Email email, String passwordHash) {
        super(id, name, email, passwordHash);
    }
}