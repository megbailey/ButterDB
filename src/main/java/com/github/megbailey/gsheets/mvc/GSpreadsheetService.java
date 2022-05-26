package com.github.megbailey.gsheets.mvc;

import com.github.megbailey.gsheets.api.GAuthentication;
import com.github.megbailey.gsheets.api.request.APIBatchRequestUtility;
import com.github.megbailey.gsheets.api.request.APIRequestUtility;
import com.github.megbailey.gsheets.api.request.APIVisualizationQueryUtility;

import com.google.api.services.sheets.v4.model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.*;

import java.util.logging.Logger;


public class GSpreadsheetService {
    private static final Logger logger = Logger.getLogger( GSpreadsheetService.class.getName() );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private String spreadsheetID;
    private APIVisualizationQueryUtility gVizRequestUtility;
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;
    private HashMap<String, GSheetService> sheets; //A spreadsheet contains a list of sheets which can be found by name

    public GSpreadsheetService(GAuthentication gAuthentication) {
        this.gVizRequestUtility = new APIVisualizationQueryUtility(gAuthentication);
        this.regularRequestUtility = APIRequestUtility.getInstance(gAuthentication);
        this.batchRequestUtility = APIBatchRequestUtility.getInstance(gAuthentication);
    }

    //this.setColumns(this.spreadsheet.getRegularService().getData(this.getName(), "$A1:$Z1").get(0));

    private void setGSheets() throws IOException {
        this.sheets = new HashMap<>();
        List<Sheet> existingSheets = this.regularRequestUtility.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet:existingSheets) {
            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();

        }
    }

    public HashMap<String, GSheetService> getGSheets() throws IOException {
        if ( this.sheets == null) {
            setGSheets();
        }
        return this.sheets;
    }

    public GSheetService getGSheet(String sheetName) {
        if (this.sheets.containsKey(sheetName))
            return this.sheets.get(sheetName);
        else
            return null;
    }

    public boolean createGSheet(String sheetName) throws IOException {
        //Check if sheet already exists to avoid an API call
        if ( !this.sheets.containsKey(sheetName) ) {

            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
            this.batchRequestUtility.executeBatch();
            GSheetService gSheet = GSheetService.build()
                    .setName(sheetName)
                    .setID(sheetID);

            //Add the new sheet to our cache (map) of sheets
            this.sheets.put( sheetName, gSheet );
            return true;
        }

        return false;
    }

    public boolean deleteGSheet(String sheetName) throws IOException {
        //Check if sheet already exists to avoid an API call
        if ( this.sheets.containsKey(sheetName) )  {

            this.batchRequestUtility.addDeleteSheetRequest( this.sheets.get(sheetName).getID() );
            this.batchRequestUtility.executeBatch();

            //Remove the sheet from our cache
            this.sheets.remove(sheetName);
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
