package com.github.megbailey.butter;

import com.fasterxml.jackson.annotation.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("SampleObjectImpl")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SampleObjectImpl implements ObjectModel {

    @JsonProperty("ID")
    private Integer ID;

    @JsonProperty("Property")
    private String property;


    public SampleObjectImpl() { }

    public SampleObjectImpl(Integer ID, String property ) {
        this.ID = ID;
        this.property = property;
    }

    public SampleObjectImpl setId(Integer ID) {
        this.ID = ID;
        return this;
    }

    public SampleObjectImpl setProperty(String property) {
        this.property = property;
        return this;
    }

    @JsonGetter(value = "ID")
    public Integer getId() {
        return this.ID;
    }

    @JsonGetter(value = "Property")
    public String getProperty() {
        return property;
    }

    public List<String> toList() {
        List<String> values = new ArrayList<>(2);
        values.add( getId().toString() );
        values.add( getProperty() );
        return values;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "ID=" + this.ID +
                ", Property='" + this.property + '\'' +
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
