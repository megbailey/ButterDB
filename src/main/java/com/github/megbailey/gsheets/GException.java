package com.github.megbailey.gsheets;

public class GException extends Exception {

    public GException() {
        super();
    }

    public GException(String message, Throwable cause) {
        super(message, cause);
    }

    public GException(String message) {
        super(message);
    }

    public GException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
