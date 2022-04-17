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

public class GSheets extends GSpreadsheet {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String name;


    public GSheets(String spreadsheetID) {
        super(spreadsheetID);
        this.name = "Sheet1";
    }

    public GSheets(String spreadsheetID, String name) {
        super(spreadsheetID);
        this.name = name;
    }


    public List<List<Object>> getData(String range) throws IOException {
        ValueRange response = this.sheetsService.spreadsheets().values()
                .get(this.spreadsheetID, range)
                .execute();
        return response.getValues();
    }


}