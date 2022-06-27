package com.github.megbailey.google.exception;

public class ColumnNotFoundException extends Exception {

    public ColumnNotFoundException() {
        super();
    }

    public ColumnNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColumnNotFoundException(String message) {
        super(message);
    }

    public ColumnNotFoundException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}