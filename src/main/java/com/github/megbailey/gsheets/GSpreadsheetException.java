package com.github.megbailey.gsheets;

public class GSpreadsheetException extends Exception {

    public GSpreadsheetException() {
        super();
    }

    public GSpreadsheetException(String message, Throwable cause) {
        super(message, cause);
    }

    public GSpreadsheetException(String message) {
        super(message);
    }

    public GSpreadsheetException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }
}
