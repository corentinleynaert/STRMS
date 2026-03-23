package com.strms.domain.model;

public class AdminRole implements UserRole {
    @Override
    public String getIdentifier() {
        return "ADMIN";
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }

    @Override
    public boolean canCreateTask() {
        return true;
    }

    @Override
    public boolean canDeleteTask() {
        return true;
    }

    @Override
    public boolean canAssignTask() {
        return true;
    }

    @Override
    public boolean canUpdateTask() {
        return true;
    }

    @Override
    public boolean canChangeTaskStatus() {
        return false;
    }

    @Override
    public boolean canGenerateReports() {
        return true;
    }
}
