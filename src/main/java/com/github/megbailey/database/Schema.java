package com.github.megbailey.database;

import java.util.List;

interface Schema {

    Table getTable();
    void setTable(Table table);

    List<Column> getColumns();
    void setColumns(List<Column> columns);
}
