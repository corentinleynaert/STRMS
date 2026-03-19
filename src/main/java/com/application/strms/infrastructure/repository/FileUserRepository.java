package com.application.strms.infrastructure.repository;

import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.infrastructure.persistence.FileHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUserRepository implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private final FileHandler fileHandler;

    public FileUserRepository(FileHandler fileHandler) {
        this.fileHandler = fileHandler;

        List<User> loadedUsers = fileHandler.load("users.txt", this::mapLineToUser);

        for (User user : loadedUsers) {
            users.put(user.id().value(), user);
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

    @Override
    public UserAuth findAuthByEmail(Email email) {
        List<UserAuth> auths = fileHandler.load("users.txt", this::mapLineToUserAuth);

        for (UserAuth auth : auths) {
            User user = findById(auth.id());
            if (user != null && user.email().equals(email)) {
                return auth;
            }
        }

        return null;
    }

    @Override
    public void addUser(User user, UserAuth userAuth) {
        users.put(user.id().value(), user);
        fileHandler.save("users.txt", List.of(new UserLine(user, userAuth)), this::mapUserLineToString);
    }

    private User findById(UserId id) {
        return users.get(id.value());
    }

    private User mapLineToUser(String line) {
        String[] parts = line.split(";");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }

        UserId id = new UserId(Integer.parseInt(parts[0]));
        String name = parts[1];
        Email email = new Email(parts[2]);
        String role = parts[4].toUpperCase();

        return createUserByRole(id, name, email, role);
    }

    private UserAuth mapLineToUserAuth(String line) {
        String[] parts = line.split(";");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }

        UserId id = new UserId(Integer.parseInt(parts[0]));
        String passwordHash = parts[3];

        return new UserAuth(id, passwordHash);
    }

    private String mapUserLineToString(UserLine userLine) {
        User user = userLine.user();
        UserAuth auth = userLine.auth();

        return user.id().value() + ";" +
                user.name() + ";" +
                user.email().value() + ";" +
                auth.passwordHash() + ";" +
                user.role();
    }

    private User createUserByRole(UserId id, String name, Email email, String role) {
        return switch (role) {
            case "ADMIN" -> new Admin(id, name, email);
            case "MANAGER" -> new Manager(id, name, email);
            case "ENGINEER" -> new Engineer(id, name, email);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }

    private record UserLine(User user, UserAuth auth) {
    }
}