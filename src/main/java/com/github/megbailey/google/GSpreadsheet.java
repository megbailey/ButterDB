package com.github.megbailey.google;

import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.api.GAuthentication;
import com.github.megbailey.google.api.request.APIBatchRequestUtility;
import com.github.megbailey.google.api.request.APIRequestUtility;
import com.github.megbailey.google.api.request.APIVisualizationQueryUtility;
import com.github.megbailey.google.exception.*;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.*;

public class GSpreadsheet {
    private final GAuthentication gAuthentication;
    // Determines the starting cell for each sheet/data table in this spreadsheet.
    // This is used to search for attributes/column labels & to perform insertions.
    private final String tableStartRange = "$A1:$Z1";
    //A spreadsheet contains a list of sheets which can be found by name
    private HashMap<String, GSheet> gSheets;
    //Utilities to help perform the API calls to google
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
            data = this.regularRequestUtility.getData(sheetName, this.tableStartRange);

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

    public GSheet getGSheet(String sheetName) throws ResourceNotFoundException {
        //Check if sheet already exists to avoid an API call
        if (!this.gSheets.containsKey(sheetName))
            throw new ResourceNotFoundException();
        return this.gSheets.get(sheetName);
    }

    public GSheet addGSheet(String sheetName) throws IOException, ResourceAlreadyExistsException {
        //Check if sheet already exists to avoid an API call
        if ( this.gSheets.containsKey(sheetName) ) {
            throw new ResourceAlreadyExistsException();
        }

        Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
        GSheet gSheet = new GSheet()
                .setName(sheetName)
                .setID(sheetID);
        this.batchRequestUtility.executeBatch();

        //Add the new sheet to our cache/map of sheets
        gSheets.put( sheetName, gSheet );
        return gSheet;

    }

    public GSheet deleteGSheet(String sheetName) throws IOException, ResourceNotFoundException {

        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        GSheet removeMe =  this.gSheets.get(sheetName);
        this.batchRequestUtility.addDeleteSheetRequest( removeMe.getID() );
        this.batchRequestUtility.executeBatch();

        //Remove the sheet from our cache
        gSheets.remove( sheetName );
        return removeMe;

    }

    public JsonArray executeQuery(String className)
            throws GAccessException, ResourceNotFoundException {

        if ( !this.gSheets.containsKey( className ) ) {
            throw new ResourceNotFoundException();
        }

        GSheet gSheet = this.gSheets.get( className );

        String gVizQuery = this.gVizRequestUtility.buildQuery( gSheet.getColumnMap() );
        JsonArray results = this.gVizRequestUtility.executeGVizQuery(gSheet, gVizQuery);
        return results;

    }

    public JsonArray executeQuery(String className, String constraints)
            throws GAccessException, ResourceNotFoundException, NullPointerException {

        if ( !this.gSheets.containsKey(className) ) {
            throw new ResourceNotFoundException();
        }
        GSheet gSheet = this.gSheets.get(className);

        String gVizQuery = this.gVizRequestUtility.buildQuery(gSheet.getColumnMap(), constraints);
        JsonArray results = this.gVizRequestUtility.executeGVizQuery(gSheet, gVizQuery);
        return results;
    }

    public List<ObjectModel> insert(String sheetName, List<ObjectModel> objects)
            throws InvalidInsertionException, ResourceNotFoundException {
        if ( !this.gSheets.containsKey(sheetName) ) {
            throw new ResourceNotFoundException();
        }
        return this.regularRequestUtility.appendRows(sheetName, this.tableStartRange, objects);

    }


    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, GSheet.class);
    }

}
