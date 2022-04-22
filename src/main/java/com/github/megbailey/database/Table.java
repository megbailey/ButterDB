package com.github.megbailey.database;

import java.util.List;

interface Table {
    Schema getSchema();
    void setSchema(Schema schema);

    List<Column> getColumns();
    void setColumns(List<Column> columns);

    List<Row> getRows();
    void setRows(List<Row> rows);

    PrimaryKey getPrimaryKey();
    void getPrimaryKey(PrimaryKey primaryKey);

    ForeignKey getForeignKey();
    void getForeignKey(ForeignKey foreignKey);
}
