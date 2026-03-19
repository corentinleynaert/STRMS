package com.application.strms.domain.model;

public class UserId {
    private final Integer value;
    private static Integer current_value = 0;

    public UserId(Integer value) {
        if (value == null)
            throw new IllegalArgumentException("Id cannot be null");

        this.value = value;

        if (value > current_value) {
            current_value = value + 1;
        }
    }

    public UserId() {
        this.value = current_value;
        current_value++;
    }

    public Integer value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserId userId))
            return false;

        return value.equals(userId.value);
    }
}