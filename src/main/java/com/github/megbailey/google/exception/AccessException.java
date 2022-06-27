package com.github.megbailey.google.exception;

public class AccessException extends Exception {

    public AccessException() {
        super();
    }

    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessException(String message) {
        super(message);
    }

    public AccessException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}