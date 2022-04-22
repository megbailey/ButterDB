package com.github.megbailey.database;

interface Column {

    String getName();
    void setName(String dataName);

    String getType();
    void setType(String dataType);

    boolean getIsPrimaryKey();
    void setIsPrimaryKey(boolean isPrimaryKey);

    boolean getIsForeignKey();
    void setIsForeignKey(boolean isForeignKey);

    PrimaryKey getPrimaryKey();
    void setPrimaryKey(PrimaryKey primaryKey);

    ForeignKey getForeignKey();
    void setForeignKey(ForeignKey foreignKey);

    boolean getAutoIncrement();
    void setAutoIncrement(boolean autoIncrement);
}
