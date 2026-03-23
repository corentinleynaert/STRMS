package com.strms.infrastructure.security;

import com.strms.domain.model.Password;
import com.strms.domain.service.PasswordHasher;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptPasswordHasher implements PasswordHasher {
    private static final int WORK_FACTOR = 10;

    @Override
    public String hash(Password password) {
        return BCrypt.hashpw(password.toString(), BCrypt.gensalt(WORK_FACTOR));
    }

    @Override
    public boolean verify(Password password, String storedHash) {
        return BCrypt.checkpw(password.toString(), storedHash);
    }
}