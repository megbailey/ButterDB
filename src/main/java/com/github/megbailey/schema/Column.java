package com.github.megbailey.schema;

public class Column {

    private String name;
    private String type;
    private Boolean isPrimaryKey;
    private Boolean isForeignKey;

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Column(String name, String type, Boolean isPrimaryKey, Boolean isForeignKey) {
        this.name = name;
        this.type = type;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Boolean getPrimaryKey() {
        return isPrimaryKey;
    }

    public Boolean getForeignKey() {
        return isForeignKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public void setForeignKey(Boolean foreignKey) {
        isForeignKey = foreignKey;
    }
}
