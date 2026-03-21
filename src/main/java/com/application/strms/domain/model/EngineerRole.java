package com.application.strms.domain.model;

public class EngineerRole implements UserRole {
    @Override
    public String getIdentifier() {
        return "ENGINEER";
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }

    @Override
    public boolean canCreateTask() {
        return false;
    }

    @Override
    public boolean canDeleteTask() {
        return false;
    }

    @Override
    public boolean canAssignTask() {
        return false;
    }

    @Override
    public boolean canUpdateTask() {
        return true;
    }

    @Override
    public boolean canChangeTaskStatus() {
        return true;
    }

    @Override
    public boolean canGenerateReports() {
        return false;
    }
}
