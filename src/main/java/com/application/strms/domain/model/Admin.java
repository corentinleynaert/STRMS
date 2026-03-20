package com.application.strms.domain.model;

public class Admin extends User {
    public Admin(Ulid id, String name, Email email) {
        super(id, name, email, "ADMIN");
    }
    public Admin(String name, Email email) {
        super(name, email, "ADMIN");
    }
}