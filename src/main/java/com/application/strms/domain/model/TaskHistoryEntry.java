package com.application.strms.domain.model;

import java.time.LocalDateTime;

public class TaskHistoryEntry {
    private final String action;
    private final User performedBy;
    private final LocalDateTime timestamp;
    private final String fieldChanged;
    private final String oldValue;
    private final String newValue;

    public TaskHistoryEntry(String action, User performedBy, LocalDateTime timestamp) {
        if (action == null || action.isBlank())
            throw new IllegalArgumentException("Action cannot be empty");
        if (performedBy == null)
            throw new IllegalArgumentException("User cannot be null");
        if (timestamp == null)
            throw new IllegalArgumentException("Timestamp cannot be null");

        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.fieldChanged = null;
        this.oldValue = null;
        this.newValue = null;
    }

    public TaskHistoryEntry(String action, User performedBy, LocalDateTime timestamp, String fieldChanged,
            String oldValue, String newValue) {
        if (action == null || action.isBlank())
            throw new IllegalArgumentException("Action cannot be empty");
        if (performedBy == null)
            throw new IllegalArgumentException("User cannot be null");
        if (timestamp == null)
            throw new IllegalArgumentException("Timestamp cannot be null");

        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
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

    public String getFieldChanged() {
        return fieldChanged;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        if (fieldChanged != null && oldValue != null && newValue != null) {
            return action + " [" + fieldChanged + ": " + oldValue + " → " + newValue + "] by "
                    + performedBy.getName() + " at " + timestamp;
        }
        return action + " by " + performedBy.getName() + " at " + timestamp;
    }
}