package com.application.strms.application.service;

import com.application.strms.application.result.LoginResult;
import com.application.strms.domain.model.Email;
import com.application.strms.domain.model.Password;
import com.application.strms.domain.model.User;
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
}