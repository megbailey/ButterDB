package com.github.megbailey.butter.domain;

import com.fasterxml.jackson.annotation.*;
import com.github.megbailey.butter.google.GSpreadsheet;

@JsonTypeName("SampleObjectModel")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SampleObjectModel extends DataModel {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("class_name")
    private String name;

    @JsonProperty("class_code")
    private String code;

    @JsonProperty("year")
    private Integer year;

    public SampleObjectModel(GSpreadsheet spreadsheet) {
        super(spreadsheet);
    }

    @JsonSetter(value = "id")
    public SampleObjectModel setId(Integer id) {
        this.id = id;
        return this;
    }

    @JsonGetter(value = "id")
    public Integer getId() {
        return this.id;
    }


    @JsonSetter(value = "class_name")
    public SampleObjectModel setName(String name) {
        this.name = name;
        return this;
    }

    @JsonGetter(value = "class_name")
    public String getName() {
        return name;
    }

    @JsonSetter(value = "class_code")
    public SampleObjectModel setCode(String code) {
        this.code = code;
        return this;
    }

    @JsonGetter(value = "class_code")
    public String getCode() {
        return code;
    }

    @JsonSetter(value = "year")
    public SampleObjectModel setYear(Integer year) {
        this.year = year;
        return this;
    }

    @JsonGetter(value = "year")
    public Integer getYear() {
        return this.year;
    }


    @Override
    public String toString() {
        return "SampleObjectModel{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", code='" + this.code + '\'' +
                ", year='" + this.year + '\'' +
                '}';
    }

}
