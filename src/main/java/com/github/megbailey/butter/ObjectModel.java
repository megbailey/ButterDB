package com.github.megbailey.butter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface ObjectModel extends Serializable {

    List<String> toList();

    String toJson();


}
