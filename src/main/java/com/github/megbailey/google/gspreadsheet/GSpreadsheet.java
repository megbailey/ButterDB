package com.github.megbailey.google.gspreadsheet;

import com.github.megbailey.google.api.GAuthentication;
import com.github.megbailey.google.api.request.APIBatchRequestUtility;
import com.github.megbailey.google.api.request.APIRequestUtility;
import com.github.megbailey.google.api.request.APIVisualizationQueryUtility;
import com.github.megbailey.google.gsheet.GSheet;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GSpreadsheet {
    private final GAuthentication gAuthentication;
    private HashMap<String, GSheet> gSheets; //A spreadsheet contains a list of sheets which can be found by name
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;
    private APIVisualizationQueryUtility gVizRequestUtility;

    public GSpreadsheet(GAuthentication gAuthentication) throws IOException{
        this.gAuthentication = gAuthentication;
        this.regularRequestUtility = new APIRequestUtility(gAuthentication);
        this.batchRequestUtility = new APIBatchRequestUtility(gAuthentication);
        this.gVizRequestUtility = new APIVisualizationQueryUtility(gAuthentication);
        initGSheets();
    }

    private void initGSheets() throws IOException{
        HashMap gSheets = new HashMap<>();
        GSheet gSheet; List<List<Object>> data; List<Object> columns;
        List<Sheet> sheets = this.regularRequestUtility.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet: sheets) {

            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();

            // grab the cells in the first row, these are the columns/attributes
            data = this.regularRequestUtility.getData(sheetName, "$A1:$Z1");

            // if columns dont exist
            if (data != null)
                columns = data.get(0);
            else
                columns = null;

            gSheet = new GSheet()
                    .setName(sheetName)
                    .setID(sheetID)
                    .setColumns(columns);
            gSheets.put( sheetName, gSheet );
        }
        this.gSheets = gSheets;
    }

    public HashMap<String, GSheet> getGSheets() {
        return gSheets;
    }

    public GSheet getGSheet(String sheetName) {
        //Check if sheet already exists to avoid an API call
        if (this.gSheets.containsKey(sheetName))
            return this.gSheets.get(sheetName);
        else
            return null;
    }

    public boolean createGSheet(String sheetName) throws IOException {
        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey(sheetName) ) {

            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
            this.batchRequestUtility.executeBatch();
            GSheet gSheet = new GSheet()
                    .setName(sheetName)
                    .setID(sheetID);

            //Add the new sheet to our cache/map of sheets
            gSheets.put( sheetName, gSheet );
            return true;
        }

        return false;
    }

    public boolean deleteGSheet(String sheetName) throws IOException {
        //Check if sheet already exists to avoid an API call
        if ( this.gSheets.containsKey( sheetName ) )  {

            this.batchRequestUtility.addDeleteSheetRequest( this.gSheets.get(sheetName).getID() );
            this.batchRequestUtility.executeBatch();

            //Remove the sheet from our cache
            gSheets.remove( sheetName );
            return true;
        }
        return false;
    }

    public JsonArray executeQuery(String className) throws IOException {

        if ( this.gSheets.containsKey( className ) ) {
            GSheet gSheet = this.gSheets.get( className );
            Integer sheetID = this.gSheets.get( className ).getID();

            String gVizQuery = this.gVizRequestUtility.buildQuery( gSheet.getColumnMap() );
            JsonArray ar = this.gVizRequestUtility.executeGVizQuery( sheetID, gVizQuery );
            return ar;
        } else {
            return null;
        }
    }

    public JsonArray executeQuery(String className, String constraints) throws IOException {
        if (this.gSheets.containsKey(className)) {

            GSheet gSheet = this.gSheets.get(className);
            Integer sheetID = gSheet.getID();

            String gVizQuery = this.gVizRequestUtility.buildQuery(gSheet.getColumnMap(), constraints);
            JsonArray ar = this.gVizRequestUtility.executeGVizQuery(sheetID, gVizQuery);
            return ar;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, GSheet.class);
    }

}
