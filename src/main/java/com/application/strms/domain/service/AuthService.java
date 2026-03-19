package com.application.strms.domain.service;

import com.application.strms.domain.model.User;
import com.application.strms.infrastructure.repository.UserRepository;
import com.application.strms.utils.PasswordUtils;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean login(String email, String password) {
        User user = userRepository.findByEmail(email);

        return user != null &&
                PasswordUtils.verify(password, user.passwordHash());
    }
}