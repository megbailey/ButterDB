package com.github.megbailey.gsheets.mvc;

import com.github.megbailey.gsheets.GSheet;
import com.github.megbailey.gsheets.GSpreadsheet;
import com.github.megbailey.gsheets.api.request.APIBatchRequestUtility;
import com.github.megbailey.gsheets.api.request.APIRequestUtility;
import com.github.megbailey.gsheets.api.request.APIVisualizationQueryUtility;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GSpreadsheetManager  {

    private final GSpreadsheet gSpreadsheet;
    private APIVisualizationQueryUtility gVizRequestUtility;
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;

    public GSpreadsheetManager(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }

    private void setGSheets() throws IOException {
        HashMap gSheets = new HashMap<>();
        List<Sheet> existingSheets = this.regularRequestUtility.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet:existingSheets) {
            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();

        }
    }

    public GSheet getGSheet(String sheetName) {
        if (this.gSpreadsheet.getGSheets().containsKey(sheetName))
            return this.gSpreadsheet.getGSheet(sheetName);
        else
            return null;
    }

    public boolean createGSheet(String sheetName) throws IOException {
        HashMap gSheets = this.gSpreadsheet.getGSheets();

        //Check if sheet already exists to avoid an API call
        if ( !gSheets.containsKey(sheetName) ) {

            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
            this.batchRequestUtility.executeBatch();
            GSheet gSheet = GSheet.build()
                    .setName(sheetName)
                    .setID(sheetID);

            //Add the new sheet to our cache (map) of sheets
            gSheets.put( sheetName, gSheet );
            return true;
        }

        return false;
    }

    public boolean deleteGSheet(String sheetName) throws IOException {
        HashMap gSheets = this.gSpreadsheet.getGSheets();
        //Check if sheet already exists to avoid an API call
        if ( gSheets.containsKey(sheetName) )  {

            this.batchRequestUtility.addDeleteSheetRequest( this.gSpreadsheet.getGSheetID(( sheetName )));
            this.batchRequestUtility.executeBatch();

            //Remove the sheet from our cache
            gSheets.remove(sheetName);
            return true;
        }
        return false;
    }


    public APIRequestUtility getRegularService() { return this.regularRequestUtility; }

    public APIBatchRequestUtility getBatchService() { return this.batchRequestUtility; }

    /*
    public JsonArray filterGSheet(String query) throws IOException {

        if (this.sheets.containsKey(sheetName)) {
            Integer sheetID = this.sheets.get(sheetName).getID();
            Response response = this.gVizRequestUtility.executeGVizQuery(query, sheetID);

            return this.gVizRequestUtility.parseGVizResponse(response);
        }
        return null;
    }
    */
}
