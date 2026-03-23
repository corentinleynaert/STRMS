package com.strms.domain.model;

public class UserRoleFactory {
    private static final AdminRole adminRole = new AdminRole();
    private static final ManagerRole managerRole = new ManagerRole();
    private static final EngineerRole engineerRole = new EngineerRole();

    public static UserRole createFromIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Role identifier cannot be empty");
        }

        return switch (identifier.toUpperCase()) {
            case "ADMIN" -> adminRole;
            case "MANAGER" -> managerRole;
            case "ENGINEER" -> engineerRole;
            default -> throw new IllegalArgumentException("Unknown role: " + identifier);
        };
    }

    public static AdminRole createAdmin() {
        return adminRole;
    }

    public static ManagerRole createManager() {
        return managerRole;
    }

    public static EngineerRole createEngineer() {
        return engineerRole;
    }
}
