package com.application.strms.application.service;

import com.application.strms.application.result.AddUserResult;
import com.application.strms.application.result.LoginResult;
import com.application.strms.application.result.UpdateUserResult;
import com.application.strms.domain.exception.DuplicateEmailException;
import com.application.strms.domain.exception.InsufficientPermissionsException;
import com.application.strms.domain.exception.InvalidUserInputException;
import com.application.strms.domain.exception.UserNotFoundException;
import com.application.strms.domain.model.*;
import com.application.strms.domain.repository.UserRepository;
import com.application.strms.domain.service.PasswordHasher;

import java.io.IOException;

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public AuthService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }

    public LoginResult login(String emailRaw, String passwordRaw) throws IOException {
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
            return LoginResult.failure("Invalid email or password");
        }

        try {
            UserAuth userAuth = userRepository.findAuthByEmail(email);

            if (userAuth == null) {
                return LoginResult.failure("Authentication data not found");
            }

            if (!passwordHasher.verify(password, userAuth.getPasswordHash())) {
                return LoginResult.failure("Invalid email or password");
            }

            return LoginResult.success(user);
        } catch (IOException e) {
            throw new IOException("Error accessing authentication data during login", e);
        }
    }

    public AddUserResult addUser(User currentUser, String name, String emailRaw, String passwordRaw, String role)
            throws IOException {
        validateManageUsersPermission(currentUser);

        Email email;
        Password password;

        try {
            email = new Email(emailRaw);
            password = new Password(passwordRaw);
        } catch (IllegalArgumentException e) {
            return AddUserResult.failure("Invalid input");
        }

        try {
            if (userRepository.findByEmail(email) != null) {
                throw new DuplicateEmailException("User with email " + email + " already exists");
            }

            String passwordHashed = this.passwordHasher.hash(password);
            User newUser = createUserByRole(name, email, role);
            UserAuth userAuth = new UserAuth(newUser.getId(), passwordHashed);

            this.userRepository.addUser(newUser, userAuth);

            return AddUserResult.success();
        } catch (IOException e) {
            throw new IOException("Error accessing user repository while adding user", e);
        } catch (Exception e) {
            return AddUserResult.failure(e.getMessage());
        }
    }

    public UpdateUserResult editUser(User currentUser, Ulid userId, String name, String emailRaw, String role,
            String passwordRaw)
            throws IOException {
        validateManageUsersPermission(currentUser);

        Email email;
        Password password;

        try {
            email = new Email(emailRaw);
            password = new Password(passwordRaw);
        } catch (IllegalArgumentException e) {
            return UpdateUserResult.failure("Invalid input");
        }

        try {
            User existingUser = userRepository.findById(userId);

            if (existingUser == null) {
                throw new UserNotFoundException("User not found");
            }

            User userWithSameEmail = userRepository.findByEmail(email);
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(userId)) {
                throw new DuplicateEmailException("User with email " + email + " already exists");
            }

            User updatedUser = createUserByRole(userId, name, email, role);

            String passwordHashed = this.passwordHasher.hash(password);
            UserAuth updatedAuth = new UserAuth(userId, passwordHashed);

            userRepository.updateUser(updatedUser, updatedAuth);

            return UpdateUserResult.success(updatedUser);
        } catch (IOException e) {
            throw new IOException("Error accessing user repository while editing user", e);
        } catch (Exception e) {
            return UpdateUserResult.failure(e.getMessage());
        }
    }

    private void validateManageUsersPermission(User currentUser) {
        if (currentUser == null || !currentUser.getRole().canManageUsers()) {
            throw new InsufficientPermissionsException("Insufficient permissions");
        }
    }

    private User createUserByRole(String name, Email email, String role) {
        return createUserByRole(new Ulid(), name, email, role);
    }

    private User createUserByRole(Ulid userId, String name, Email email, String role) {
        UserRole userRole = UserRoleFactory.createFromIdentifier(role);
        return instantiateUserByRole(userId, name, email, userRole);
    }

    private User instantiateUserByRole(Ulid id, String name, Email email, UserRole role) {
        String roleId = role.getIdentifier();
        return switch (roleId) {
            case "ADMIN" -> new Admin(id, name, email);
            case "MANAGER" -> new Manager(id, name, email);
            case "ENGINEER" -> new Engineer(id, name, email);
            default -> throw new InvalidUserInputException("Unknown role: " + roleId);
        };
    }
}
