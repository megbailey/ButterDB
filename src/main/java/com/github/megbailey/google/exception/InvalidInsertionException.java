package com.github.megbailey.google.exception;

public class InvalidInsertionException extends Exception {

    public InvalidInsertionException() {
        super();
    }

    public InvalidInsertionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInsertionException(String message) {
        super(message);
    }

    public InvalidInsertionException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
