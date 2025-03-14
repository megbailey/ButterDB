package com.github.megbailey.butter.domain;

import com.fasterxml.jackson.annotation.*;
import com.github.megbailey.butter.google.GSpreadsheet;

@JsonTypeName("SampleObjectModel")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class SampleObjectModel extends DataModel {
    public static String primaryKey = "id";
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("year")
    private Integer year;

    public SampleObjectModel() {
        super();
    }

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


    @JsonSetter(value = "name")
    public SampleObjectModel setName(String name) {
        this.name = name;
        return this;
    }

    @JsonGetter(value = "name")
    public String getName() {
        return name;
    }

    @JsonSetter(value = "code")
    public SampleObjectModel setCode(String code) {
        this.code = code;
        return this;
    }

    @JsonGetter(value = "code")
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
