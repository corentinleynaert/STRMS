package com.strms.domain.exception;

public class DuplicateTaskException extends RuntimeException {
    public DuplicateTaskException(String message) {
        super(message);
    }
}
