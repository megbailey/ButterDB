package com.github.megbailey.gsheets.api;

import com.github.megbailey.gsheets.api.request.APIRequestService;

import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

public class GSheet {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final GSpreadsheet spreadsheet; //child  has knowledge of parent without inheritance
    private Integer ID;
    private String name;

    public GSheet(GSpreadsheet spreadsheet, String name, Integer ID) {
        this.spreadsheet = spreadsheet;
    }

    public String getName() {
        return this.name;
    }

    public Integer getID() {
        return this.ID;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setID(Integer newID) {
        this.ID = newID;
    }

    public List<List<Object>> getData(String range) {
        try {
            return this.spreadsheet.getRegularService().getData(this.name, range);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}