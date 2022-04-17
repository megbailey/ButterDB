package com.github.megbailey.schema;

import java.util.ArrayList;
import java.util.List;

public class Schema {
    // Schema is a list of fields and their type
    private List<Field> listOfFields;

    public Schema() {
        this.listOfFields = new ArrayList<>();
    }

    public Schema(List<Field> listOfFields) {
        this.listOfFields = listOfFields;
    }

    public List<Field> getListOfFields() {
        return this.listOfFields;
    }

    public void setListOfFields(List<Field> fields) {
        this.listOfFields = fields;
    }

    public void addField(Field field) {
        this.listOfFields.add(field);
    }

    public void removeField(Field field){
        this.listOfFields.remove(field);
    }
}
