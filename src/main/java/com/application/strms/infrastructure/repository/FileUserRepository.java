package com.application.strms.infrastructure.repository;

import com.application.strms.domain.model.Email;
import com.application.strms.domain.model.User;
import com.application.strms.domain.model.UserId;
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

        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid line: " + line);
        }

        return new User(
                new UserId(Integer.parseInt(parts[0])),
                parts[1],
                new Email(parts[2]),
                parts[3]
        );
    }
}