package com.application.strms.domain.service;

import com.application.strms.domain.model.Password;

public interface PasswordHasher {
    String hash(Password password);
    boolean verify(Password password, String storedHash);
}