package com.strms.domain.model;

public class ManagerRole implements UserRole {
    @Override
    public String getIdentifier() {
        return "MANAGER";
    }

    @Override
    public boolean canManageUsers() {
        return false;
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
