package com.strms.domain.exception;

public class FilePersistenceException extends RuntimeException {
    public FilePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
