package com.strms.domain.exception;

public class DependencyNotCompletedException extends RuntimeException {
    public DependencyNotCompletedException(String message) {
        super(message);
    }
}
