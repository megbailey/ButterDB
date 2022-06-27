package com.github.megbailey.google.exception;

public class EmptyContentException extends Exception {

    public EmptyContentException() {
        super();
    }

    public EmptyContentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyContentException(String message) {
        super(message);
    }

    public EmptyContentException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}