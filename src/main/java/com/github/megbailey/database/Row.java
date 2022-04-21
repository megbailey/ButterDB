package com.github.megbailey.database;

import java.util.ArrayList;
import java.util.List;

public class Row {
    private List<Field> fields;

    public Row() {
        this.fields = new ArrayList<>();
    }

    public Row(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getFields() {
        return this.fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public void removeField(Field field) {
        this.fields.remove(field);
    }
}
