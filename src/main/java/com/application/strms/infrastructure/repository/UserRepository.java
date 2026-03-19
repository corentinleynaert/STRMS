package com.application.strms.infrastructure.repository;

import com.application.strms.domain.model.User;
import com.application.strms.utils.FileHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private final Map<Integer, User> users = new HashMap<>();

    public UserRepository() {
        List<User> loadedUsers = FileHandler.load("users.txt", User::fromLine);

        for (User user : loadedUsers) {
            users.put(user.id(), user);
        }
    }

    public User findByEmail(String email) {
        for (User user : users.values()) {
            if (user.email().equals(email)) {
                return user;
            }
        }

        return null;
    }
}