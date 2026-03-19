package com.application.strms.domain.model;

public class Password {
    private final String value;

    public Password(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}