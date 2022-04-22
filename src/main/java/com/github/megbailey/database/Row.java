package com.github.megbailey.database;

import java.util.ArrayList;
import java.util.List;

interface Row {

    List<Field> getFields();
    void setFields(List<Field> fields);

    void addField(Field field);
    void removeField(Field field);
}
