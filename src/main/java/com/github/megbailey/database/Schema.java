package com.github.megbailey.database;

import com.github.megbailey.database.Column;

import java.util.ArrayList;
import java.util.List;

public class Schema extends Row {
    // Schema is a special type of row which holds columns, a child of fields
    // Schema consists of columns, their type, and attributes

    public Schema() {
        super();
    }

    public Schema(List<Field> columns) {
        super(columns);
    }
}
