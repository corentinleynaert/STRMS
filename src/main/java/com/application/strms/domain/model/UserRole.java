package com.application.strms.domain.model;

public interface UserRole {
    String getIdentifier();

    boolean canManageUsers();
}
