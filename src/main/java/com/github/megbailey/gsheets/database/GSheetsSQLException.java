package com.github.megbailey.gsheets.database;

import net.sf.jsqlparser.JSQLParserException;

public class GSheetsSQLException extends JSQLParserException {

    public GSheetsSQLException() {
        super();
    }

    public GSheetsSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public GSheetsSQLException(String message) {
        super(message);
    }

    public GSheetsSQLException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }

}
