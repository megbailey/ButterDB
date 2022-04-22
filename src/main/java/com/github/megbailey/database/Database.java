package com.github.megbailey.database;

interface Database {
    void connect();
    Table createTable();
    void setTable(Table table);
}
