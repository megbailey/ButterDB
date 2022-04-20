package com.github.megbailey.gsheets.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonParser;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

public class GSheets {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Integer ID;
    private Sheets sheetsService;
    private String name;
    //private String dataRange;

    public GSheets( Sheets sheetsService, String name, Integer ID) {
        this.sheetsService = sheetsService;
        this.ID = ID;
        this.name = name;
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

    public void setData() throws IOException {

    }

    /*
    public List<List<Object>> getData(String range) throws IOException {
        ValueRange response = this.sheetsService.spreadsheets().values()
                .get(this.spreadsheetID, range)
                .execute();
        return response.getValues();
    }
    */

}