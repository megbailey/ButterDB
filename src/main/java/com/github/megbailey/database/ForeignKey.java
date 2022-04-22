package com.github.megbailey.database;

interface ForeignKey {

    Table getReferenceTable();
    void setReferenceTable(Table referenceTable);

    Column getReferenceColumn();
    void setReferenceColumn(Column referenceColumn);


    //actions
}
