package com.streat.strms;

import java.util.regex.Pattern;

public class Validator {
    public static boolean checkEmail(String email) {
        return Pattern
                .compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                .matcher(email)
                .matches();
    }
}
