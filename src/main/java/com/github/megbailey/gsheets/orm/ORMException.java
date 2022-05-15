package com.github.megbailey.gsheets.orm;

import net.sf.jsqlparser.JSQLParserException;

public class ORMException extends JSQLParserException {

    public ORMException() {
        super();
    }

    public ORMException(String message, Throwable cause) {
        super(message, cause);
    }

    public ORMException(String message) {
        super(message);
    }

    public ORMException(Throwable cause) {
        super(cause == null ? null : cause.getMessage(), cause);
    }

}
