package com.application.strms.domain.model;

import java.time.LocalDateTime;

public class TaskHistoryEntry {
    private final String action;
    private final User performedBy;
    private final LocalDateTime timestamp;

    public TaskHistoryEntry(String action, User performedBy, LocalDateTime timestamp) {
        if (action == null || action.isBlank()) throw new IllegalArgumentException("Action cannot be empty");
        if (performedBy == null) throw new IllegalArgumentException("User cannot be null");
        if (timestamp == null) throw new IllegalArgumentException("Timestamp cannot be null");

        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}