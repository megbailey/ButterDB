package com.github.megbailey.schema;

import java.util.ArrayList;
import java.util.List;

public class Schema {
    // Schema is a list of fields and their type
    private List<Column> listOfFields;

    public Schema() {
        this.listOfFields = new ArrayList<>();
    }

    public Schema(List<Column> listOfFields) {
        this.listOfFields = listOfFields;
    }

    public List<Column> getListOfFields() {
        return this.listOfFields;
    }

    public void setListOfFields(List<Column> fields) {
        this.listOfFields = fields;
    }

    public void addField(Column field) {
        this.listOfFields.add(field);
    }

    public void removeField(Column column){
        this.listOfFields.remove(column);
    }
}
