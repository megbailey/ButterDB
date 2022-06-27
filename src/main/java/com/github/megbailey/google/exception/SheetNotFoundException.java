package com.github.megbailey.google.exception;

public class SheetNotFoundException extends Exception {

    public SheetNotFoundException() {
        super();
    }

    public SheetNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SheetNotFoundException(String message) {
        super(message);
    }

    public SheetNotFoundException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
