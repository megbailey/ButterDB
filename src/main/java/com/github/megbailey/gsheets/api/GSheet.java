package com.github.megbailey.gsheets.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GSheet {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final GSpreadsheet spreadsheet;
    private Integer Id;
    private String name;
    private Map<String, String> columnMap;

    /* TODO:
    * - map the label name to the column ID (letter) for graph viz queries
    * - figure out how keep track of the filled range
    * - append data to next available row in spreadsheet
    */

    public GSheet(GSpreadsheet spreadsheet, String name, Integer Id) throws IOException {
        this.spreadsheet = spreadsheet;
        this.name = name;
        this.Id = Id;
        //List<List<Object>> data = spreadsheet.getRegularService().getData(this.name, "1:1");
        //System.out.println( data.toString() );
    }

    public String getName() {
        return this.name;
    }

    public Integer getID() {
        return this.Id;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setID(Integer newId) {
        this.Id = newId;
    }

    public Map<String, String> getColumnMap() {
        return this.columnMap;
    }


}