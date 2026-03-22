package com.application.strms.domain.exception;

public class FilePersistenceException extends RuntimeException {
    public FilePersistenceException(String message) {
        super(message);
    }

    public FilePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
