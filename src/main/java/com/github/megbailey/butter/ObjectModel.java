package com.github.megbailey.butter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface ObjectModel extends Serializable {

    List<String> toList();

    @JsonValue
    String toJson() throws JsonProcessingException;


}
