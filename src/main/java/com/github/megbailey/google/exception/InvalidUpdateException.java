package com.github.megbailey.google.exception;

public class InvalidUpdateException extends Exception {

    public InvalidUpdateException() {
        super();
    }

    public InvalidUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUpdateException(String message) {
        super(message);
    }

    public InvalidUpdateException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
