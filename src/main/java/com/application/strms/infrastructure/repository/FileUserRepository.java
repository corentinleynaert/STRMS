package com.application.strms.infrastructure.repository;

import com.application.strms.domain.model.Admin;
import com.application.strms.domain.model.Email;
import com.application.strms.domain.model.Engineer;
import com.application.strms.domain.model.Manager;
import com.application.strms.domain.model.Ulid;
import com.application.strms.domain.model.User;
import com.application.strms.domain.model.UserAuth;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.infrastructure.persistence.FileHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUserRepository implements UserRepository {
    private final Map<Ulid, User> users = new HashMap<>();
    private final FileHandler fileHandler;

    public FileUserRepository(FileHandler fileHandler) throws IOException {
        this.fileHandler = fileHandler;

        try {
            List<User> loadedUsers = fileHandler.load("users.txt", this::mapLineToUser);

            for (User user : loadedUsers) {
                users.put(user.getId(), user);
            }
        } catch (IOException e) {
            throw new IOException("Failed to initialize user repository from file", e);
        }
    }

    @Override
    public User findByEmail(Email email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findById(Ulid id) {
        return users.get(id);
    }

    @Override
    public UserAuth findAuthByEmail(Email email) throws IOException {
        try {
            List<UserAuth> auths = fileHandler.load("users.txt", this::mapLineToUserAuth);

            for (UserAuth auth : auths) {
                User user = findById(auth.getId());
                if (user != null && user.getEmail().equals(email)) {
                    return auth;
                }
            }

            return null;
        } catch (IOException e) {
            throw new IOException("Failed to find authentication by email: " + email, e);
        }
    }

    @Override
    public void addUser(User user, UserAuth userAuth) throws IOException {
        try {
            users.put(user.getId(), user);
            fileHandler.save("users.txt", List.of(new UserLine(user, userAuth)), this::mapUserLineToString);
        } catch (IOException e) {
            throw new IOException("Failed to add user: " + user.getId(), e);
        }
    }

    @Override
    public void updateUser(User user, UserAuth userAuth) throws IOException {
        try {
            if (!user.getId().equals(userAuth.getId())) {
                throw new IllegalArgumentException("User ID and UserAuth ID must be the same");
            }

            User existingUser = users.get(user.getId());

            if (existingUser == null) {
                throw new IllegalArgumentException("User not found: " + user.getId());
            }

            users.put(user.getId(), user);

            List<UserAuth> auths = fileHandler.load("users.txt", this::mapLineToUserAuth);
            List<UserLine> updatedLines = auths.stream()
                    .map(auth -> {
                        User current = users.get(auth.getId());
                        UserAuth authToUse = auth.getId().equals(user.getId()) ? userAuth : auth;
                        return new UserLine(current, authToUse);
                    })
                    .toList();

            fileHandler.replaceAll("users.txt", updatedLines, this::mapUserLineToString);
        } catch (IOException e) {
            throw new IOException("Failed to update user: " + user.getId(), e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return users.values().stream().toList();
    }

    private User mapLineToUser(String line) {
        String[] parts = line.split(";");

        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }

        Ulid id = Ulid.fromString(parts[0]);
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

        Ulid id = Ulid.fromString(parts[0]);
        String passwordHash = parts[3];

        return new UserAuth(id, passwordHash);
    }

    private String mapUserLineToString(UserLine userLine) {
        User user = userLine.user();
        UserAuth auth = userLine.auth();

        return user.getId().toString() + ";" +
                user.getName() + ";" +
                user.getEmail() + ";" +
                auth.getPasswordHash() + ";" +
                user.getRole();
    }

    private User createUserByRole(Ulid id, String name, Email email, String role) {
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