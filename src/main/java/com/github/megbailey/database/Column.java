package com.github.megbailey.database;

public class Column extends Field {

    private String dataType;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;

    public Column(String dataName, String dataType) {
        super(dataName);
        this.dataType = dataType;
    }

    public Column(String dataName, String dataType, Boolean isPrimaryKey, Boolean isForeignKey) {
        super(dataName);
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
    }

    public String getDataType() {
        return dataType;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public Boolean getForeignKey() {
        return isForeignKey;
    }

    public void setType(String dataType) {
        this.dataType = dataType;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public void setForeignKey(Boolean foreignKey) {
        isForeignKey = foreignKey;
    }
}
