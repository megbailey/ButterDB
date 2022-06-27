package com.github.megbailey.google.exception;

public class InvalidQueryException extends Exception {
    public InvalidQueryException() {
        super();
    }

    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidQueryException(String message) {
        super(message);
    }

    public InvalidQueryException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
