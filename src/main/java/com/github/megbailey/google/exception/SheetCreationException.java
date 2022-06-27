package com.github.megbailey.google.exception;

public class SheetCreationException extends Exception {

    public SheetCreationException() {
        super();
    }

    public SheetCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SheetCreationException(String message) {
        super(message);
    }

    public SheetCreationException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
