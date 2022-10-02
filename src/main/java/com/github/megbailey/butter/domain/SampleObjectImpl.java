package com.github.megbailey.butter.domain;

import com.fasterxml.jackson.annotation.*;

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

    @JsonSetter(value = "ID")
    public SampleObjectImpl setId(Integer ID) {
        this.ID = ID;
        return this;
    }

    @JsonGetter(value = "ID")
    public Integer getId() {
        return this.ID;
    }


    @JsonSetter(value = "Property")
    public SampleObjectImpl setProperty(String property) {
        this.property = property;
        return this;
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

}
