package com.github.megbailey.database;

public class Field {
    Object value;

    public Field() { }

    public Field(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
