package com.application.strms.infrastructure.repository;

import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.infrastructure.persistence.FileHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUserRepository implements UserRepository {
    private final Map<UserId, User> users = new HashMap<>();
    private final FileHandler fileHandler;

    public FileUserRepository(FileHandler fileHandler) {
        this.fileHandler = fileHandler;

        List<User> loadedUsers = fileHandler.load("users.txt", this::mapLineToUser);

        for (User user : loadedUsers) {
            users.put(user.id(), user);
        }
    }

    @Override
    public User findByEmail(Email email) {
        for (User user : users.values()) {
            if (user.email().equals(email)) {
                return user;
            }
        }
        return null;
    }

    private User mapLineToUser(String line) {
        String[] parts = line.split(";");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }

        UserId id = new UserId(Integer.parseInt(parts[0]));
        String name = parts[1];
        Email email = new Email(parts[2]);
        String password = parts[3];
        String role = parts[4].toUpperCase();

        return switch (role) {
            case "ADMIN" -> new Admin(id, name, email, password);
            case "MANAGER" -> new Manager(id, name, email, password);
            case "ENGINEER" -> new Engineer(id, name, email, password);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}