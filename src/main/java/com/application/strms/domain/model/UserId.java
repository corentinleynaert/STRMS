package com.application.strms.domain.model;

import java.util.Objects;

public class UserId {
    private final Integer value;

    public UserId(Integer value) {
        if (value == null) throw new IllegalArgumentException("Id cannot be null");

        this.value = value;
    }

    public Integer value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId userId)) return false;

        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}