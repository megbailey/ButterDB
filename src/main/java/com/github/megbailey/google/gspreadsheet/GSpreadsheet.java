package com.github.megbailey.google.gspreadsheet;

import com.github.megbailey.google.api.GAuthentication;
import com.github.megbailey.google.api.request.APIBatchRequestUtility;
import com.github.megbailey.google.api.request.APIRequestUtility;
import com.github.megbailey.google.api.request.APIVisualizationQueryUtility;
import com.github.megbailey.google.gsheet.GSheet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.HashMap;

public class GSpreadsheet {
    private final GAuthentication gAuthentication;
    private HashMap<String, GSheet> gSheets; //A spreadsheet contains a list of sheets which can be found by name
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;
    private APIVisualizationQueryUtility gVizRequestUtility;

    public GSpreadsheet(GAuthentication gAuthentication) {
        this.gAuthentication = gAuthentication;
        this.regularRequestUtility = new APIRequestUtility(gAuthentication);
        this.batchRequestUtility = new APIBatchRequestUtility(gAuthentication);
        this.gVizRequestUtility = new APIVisualizationQueryUtility(gAuthentication);
    }

    public String getGSpreadsheetID() {
        return gAuthentication.getSpreadsheetID();
    }

    public HashMap<String, GSheet> getGSheets() {
        return gSheets;
    }

    public void setGSheets(HashMap<String, GSheet> sheets) {
        this.gSheets = sheets;
    }

    public GSheet getGSheet(String sheetName) {
        if (this.gSheets.containsKey(sheetName))
            return this.gSheets.get(sheetName);
        else
            return null;
    }

    public JsonArray executeSelect(String query, String className) throws IOException {
        Integer sheetID = this.gSheets.get(className).getID();
        return this.gVizRequestUtility.executeGVizQuery(query, sheetID);
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, GSheet.class);
    }

}
