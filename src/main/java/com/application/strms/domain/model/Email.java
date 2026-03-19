package com.application.strms.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final Pattern PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank() || !PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email");
        }

        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;

        return value.equals(email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}