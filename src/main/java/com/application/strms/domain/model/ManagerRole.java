package com.application.strms.domain.model;

public class ManagerRole implements UserRole {
    @Override
    public String getIdentifier() {
        return "MANAGER";
    }

    @Override
    public boolean canManageUsers() {
        return false;
    }
}
