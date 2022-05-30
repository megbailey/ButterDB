package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.api.request.APIBatchRequestUtility;
import com.github.megbailey.google.api.request.APIVisualizationQueryUtility;
import com.github.megbailey.google.gspreadsheet.GSpreadsheet;
import com.google.gson.JsonArray;

import java.io.IOException;

public class GSheetModel {
    private final GSpreadsheet gSpreadsheet;
    private APIVisualizationQueryUtility gVizRequestUtility;

    public GSheetModel() {

    }

    public JsonArray all(String tableName) throws IOException {
        return all()
    }

    public JsonArray all(Integer tableID) throws IOException {

    }

    public JsonArray create(String tableName) throws IOException {

    }

    public JsonArray delete(String tableName) throws IOException {

    }

    /*
    public JsonArray filter(String query) throws IOException {

        if (this.sheets.containsKey(sheetName)) {
            Integer sheetID = this.sheets.get(sheetName).getID();
            Response response = this.gVizRequestUtility.executeGVizQuery(query, sheetID);

            return this.gVizRequestUtility.parseGVizResponse(response);
        }
        return null;
    }
    */
}
