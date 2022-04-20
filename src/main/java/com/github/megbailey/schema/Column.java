package com.github.megbailey.schema;

public class Column {

    private String dataName;
    private String dataType;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;

    public Column(String dataName, String dataType) {
        this.dataName = dataName;
        this.dataType = dataType;
    }

    public Column(String dataName, String dataType, Boolean isPrimaryKey, Boolean isForeignKey) {
        this.dataName = dataName;
        this.dataType = dataType;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
    }

    public String getDataName() {
        return dataName;
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

    public void setName(String dataName) {
        this.dataName = dataName;
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
