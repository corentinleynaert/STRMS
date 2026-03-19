package com.application.strms.application.service;

import com.application.strms.application.result.AddUserResult;
import com.application.strms.application.result.LoginResult;
import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.domain.service.PasswordHasher;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public LoginResult login(String emailRaw, String passwordRaw) {
        Email email;
        Password password;

        try {
            email = new Email(emailRaw);
            password = new Password(passwordRaw);
        } catch (IllegalArgumentException e) {
            return LoginResult.failure("Invalid input");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return LoginResult.failure("User not found");
        }

        if (!passwordHasher.verify(password, user.passwordHash())) {
            return LoginResult.failure("Invalid password");
        }

        return LoginResult.success(user);
    }

    public AddUserResult addUser(User user, String name,String emailRaw, String passwordRaw, String role) {
        Email email;
        Password password;

        try {
            email = new Email(emailRaw);
            password = new Password(passwordRaw);
        } catch (IllegalArgumentException e) {
            return AddUserResult.failure("Invalid input");
        }

        if (userRepository.findByEmail(email) != null) {
            return AddUserResult.failure("User with email " + email.value() + " already exists");
        }

        String passwordHashed = this.passwordHasher.hash(password);

        User new_user = switch (role) {
            case "ADMIN" -> new Admin(name, email, passwordHashed);
            case "MANAGER" -> new Manager(name, email,passwordHashed);
            case "ENGINEER" -> new Engineer(name, email, passwordHashed);
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };

        try {
            this.userRepository.addUser(new_user);

            return AddUserResult.success(new_user);
        } catch (Exception e) {
            return AddUserResult.failure(e.getMessage());
        }
    }
}