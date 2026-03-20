package com.application.strms.domain.model;

public class AdminRole implements UserRole {
    @Override
    public String getIdentifier() {
        return "ADMIN";
    }

    @Override
    public boolean canManageUsers() {
        return true;
    }
}
