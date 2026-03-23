package com.strms.domain.model;

public interface UserRole {
    String getIdentifier();

    boolean canManageUsers();

    boolean canCreateTask();

    boolean canDeleteTask();

    boolean canAssignTask();

    boolean canUpdateTask();

    boolean canChangeTaskStatus();

    boolean canGenerateReports();
}
