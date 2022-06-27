package com.github.megbailey.butter;

import com.fasterxml.jackson.annotation.*;
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


    public TestObject() { }

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

    @JsonGetter(value = "Id")
    public Integer getId() {
        return id;
    }

    @JsonGetter(value = "property")
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
        System.out.println( this.toString() );
        Gson gson = new Gson();
        return gson.toJson(this, getClass());
        //ObjectMapper objectMapper = new ObjectMapper();
        // return this.id+","+this.property+","+this.getClass().getCanonicalName();
        }
}
