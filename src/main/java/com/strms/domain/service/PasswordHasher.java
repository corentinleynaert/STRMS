package com.strms.domain.service;

import com.strms.domain.model.Password;

public interface PasswordHasher {
    String hash(Password password);
    boolean verify(Password password, String storedHash);
}