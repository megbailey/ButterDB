package com.github.megbailey.google;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface ObjectModel {

    List<String> listValues();

    String toJson();

}
