package com.application.strms.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    private static final int WORK_FACTOR = 10;

    public static String hash(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        return BCrypt.hashpw(password, BCrypt.gensalt(WORK_FACTOR));
    }

    public static boolean verify(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }

        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            return false;
        }
    }
}