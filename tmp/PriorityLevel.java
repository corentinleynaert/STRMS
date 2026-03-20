package com.application.strms.domain.model;

public enum PriorityLevel {
    LOW(1), 
    MEDIUM(2), 
    HIGH(3), 
    CRITICAL(4);

    private final int value;

    PriorityLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}