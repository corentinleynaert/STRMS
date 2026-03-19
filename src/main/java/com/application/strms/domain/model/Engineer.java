package com.application.strms.domain.model;

public class Engineer extends User {
    public Engineer(UserId id, String name, Email email) {
        super(id, name, email, "ENGINEER");
    }
    
    public Engineer(String name, Email email) {
        super(name, email, "ENGINEER");
    }
}