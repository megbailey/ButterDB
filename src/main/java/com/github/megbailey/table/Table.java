package com.github.megbailey.table;

import com.github.megbailey.schema.Field;
import com.github.megbailey.schema.Schema;

import java.util.List;

public class Table {
    private String name;
    private Schema schema;

    private Table(String name, Schema schema) {
        this.name = name;
        this.schema = schema;
    }

    private Table(String name, List<Field> fieldList) {
        this.name = name;
        this.schema = new Schema(fieldList);
    }
}
