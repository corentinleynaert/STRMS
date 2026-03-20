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
}
