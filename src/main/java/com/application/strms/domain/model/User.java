package com.application.strms.domain.model;

public record User(Integer id, String name, String email, String passwordHash) {
    private static int current_id = 0;

    public User(Integer id, String name, String email, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;

        if (this.id >= current_id) {
            current_id = this.id + 1;
        }
    }

    public static Integer nextId() {
        return current_id++;
    }

    public static User fromLine(String line) {
        String[] parts = line.split(";");

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid line : " + line);
        }

        return new User(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                parts[3]
        );
    }

    public String toLine() {
        return id + ";" + name + ";" + email + ";" + passwordHash;
    }
}