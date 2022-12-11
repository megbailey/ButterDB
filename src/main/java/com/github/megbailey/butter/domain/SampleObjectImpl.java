package com.github.megbailey.butter.domain;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

@JsonTypeName("SampleObjectImpl")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SampleObjectImpl implements ObjectModel {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("class_name")
    private String name;

    @JsonProperty("class_code")
    private String code;

    @JsonProperty("year")
    private Integer year;

    public SampleObjectImpl() { }

    @JsonSetter(value = "id")
    public SampleObjectImpl setId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonGetter(value = "id")
    public Integer getId() {
        return this.id;
    }


    @JsonSetter(value = "class_name")
    public SampleObjectImpl setName(String name) {
        this.name = name;
        return this;
    }

    @JsonGetter(value = "class_name")
    public String getName() {
        return name;
    }

    @JsonSetter(value = "class_code")
    public SampleObjectImpl setCode(String code) {
        this.code = code;
        return this;
    }

    @JsonGetter(value = "class_code")
    public String getCode() {
        return code;
    }

    @JsonSetter(value = "year")
    public SampleObjectImpl setYear(Integer year) {
        this.year = year;
        return this;
    }

    @JsonGetter(value = "year")
    public Integer getYear() {
        return this.year;
    }

    public List<String> toList() {
        List<String> values = new ArrayList<>(2);
        values.add( getId().toString() );
        values.add( getName() );
        values.add( getCode() );
        values.add( getYear().toString() );
        return values;
    }

    @Override
    public String toString() {
        return "SampleObjectImpl{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", code='" + this.code + '\'' +
                ", year='" + this.year + '\'' +
                '}';
    }

}
