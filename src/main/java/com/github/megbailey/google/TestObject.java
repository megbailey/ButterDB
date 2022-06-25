package com.github.megbailey.google;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import com.github.megbailey.google.gsheet.GSheet;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("TestObject")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class TestObject implements ObjectModel {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("property")
    private String property;


    public TestObject( Integer id, String property ) {
        this.id = id;
        this.property = property;
    }

    public TestObject setId(Integer id) {
        this.id = id;
        return this;
    }

    public TestObject setProperty(String property) {
        this.property = property;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public String getProperty() {
        return property;
    }

    public List<String> listValues() {
        List<String> values = new ArrayList<>(2);
        values.add( getId().toString() );
        values.add( getProperty() );
        return values;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "id=" + this.id +
                ", property='" + this.property + '\'' +
                '}';
    }

    @JsonValue
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this, getClass());
    }
}
