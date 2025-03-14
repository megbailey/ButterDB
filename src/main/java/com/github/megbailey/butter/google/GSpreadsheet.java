
package com.github.megbailey.butter.google;

import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.api.request.APIBatchRequestUtility;
import com.github.megbailey.butter.google.api.request.APIRequestUtility;
import com.github.megbailey.butter.google.api.request.APIVisualizationQueryUtility;
import com.github.megbailey.butter.google.exception.BadResponse;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;

public class GSpreadsheet {

    // A list of sheets which can be found by name
    private HashMap<String, GSheet> gSheets;
    // Utilities to help perform the API calls to google
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;
    private APIVisualizationQueryUtility gVizRequestUtility;
    // The cell range of the last inserted value
    private String getLastInsertedRange;

    public GSpreadsheet(GAuthentication gAuthentication) throws IOException {
        this.regularRequestUtility = new APIRequestUtility(gAuthentication);
        this.batchRequestUtility = new APIBatchRequestUtility(gAuthentication);
        this.gVizRequestUtility = new APIVisualizationQueryUtility(gAuthentication);

        HashMap gSheets = new HashMap<>();
        GSheet gSheet;
        List<Sheet> sheets = this.regularRequestUtility.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet: sheets) {

            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();

            gSheet = new GSheet()
                .setName(sheetName)
                .setID(sheetID);
            gSheets.put( sheetName, gSheet );
        }
        this.gSheets = gSheets;
    }

    public GSheet firstOrNewSheet(String sheetName, List<Object> sheetColumns) throws IOException, BadRequestException {
        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey(sheetName) ) {
            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
            this.batchRequestUtility.executeBatch();
            GSheet gSheet = new GSheet()
                .setName(sheetName)
                .setID(sheetID);
            // Add the new sheet to our cache/map of sheets
            gSheets.put(sheetName, gSheet);
        }

        // Add single row to the top of the sheet that represents the columns
        if (sheetColumns != null) {
            GSheet gSheet = this.gSheets.get(sheetName)
                .setColumns(sheetColumns);
            List<List<Object>> dataToSend = new ArrayList<>(1);
            dataToSend.add(sheetColumns);
            String lastColumnCellID = gSheet.IDDictionary.get(sheetColumns.size());
            this.regularRequestUtility.update(sheetName, "$A1:$" + lastColumnCellID + "1", dataToSend);
        }

        return this.gSheets.get(sheetName);

    }

    public void updateRow(String sheetName, List<Object> row) throws ResourceNotFoundException, BadRequestException {
        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        GSheet gsheet = this.gSheets.get(sheetName);

        List<List<Object>> dataToSend = new ArrayList<>(1);
        dataToSend.add(row);
        String lastColumnCellID = gsheet.IDDictionary.get(row.size());
        this.regularRequestUtility.update(sheetName, "$A1:$" + lastColumnCellID + "1", dataToSend);
    }

    public List<List<Object>> getWithRange(String sheetName, String cellRange) throws BadRequestException, ResourceNotFoundException {
        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        return this.regularRequestUtility.getData(sheetName, cellRange);
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

    public JsonArray get(String sheetName, String gVizQuery) throws GAccessException, ResourceNotFoundException, BadResponse, IOException {
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        GSheet gSheet = this.gSheets.get( sheetName );
        // Get all
        if ( gVizQuery == null ) {
            gVizQuery = APIVisualizationQueryUtility.buildQuery( gSheet.getColumnMap() );
        }
        JsonArray results = this.gVizRequestUtility.executeGVizQuery(gSheet, gVizQuery);
        return addClassProperty(sheetName, results);

    }


    public JsonArray find(String className, String constraints) throws GAccessException, ResourceNotFoundException, BadResponse, IOException {

        if ( !this.gSheets.containsKey(className) ) {
            throw new ResourceNotFoundException();
        }
        GSheet gSheet = this.gSheets.get(className);

        String gVizQuery = this.gVizRequestUtility.buildQuery(gSheet.getColumnMap(), constraints);
        JsonArray results = this.gVizRequestUtility.executeGVizQuery(gSheet, gVizQuery);
        return addClassProperty(className, results);
    }

    public List<Object> insertRow(String sheetName, String rangeForInsert, List<Object> row) throws BadRequestException, ResourceNotFoundException
    {
        if ( !this.gSheets.containsKey(sheetName) ) {
            throw new ResourceNotFoundException();
        }

        // calculate row size
        List<List<Object>> rowToAdd = new ArrayList<>(1);
        rowToAdd.add(row);
        rowToAdd = this.regularRequestUtility.append(sheetName, rangeForInsert, rowToAdd);
        this.getLastInsertedRange = rangeForInsert;
        return rowToAdd.get(0);
    }

    /*public boolean delete(String sheetName, String queryStr)
            throws GAccessException, ResourceNotFoundException, IOException, BadResponse {
        if ( !this.gSheets.containsKey(sheetName) ) {
            throw new ResourceNotFoundException();
        }
        JsonArray queryResults = find(sheetName, queryStr);
        // Element does not exist
        if (queryResults.isEmpty())
            throw new ResourceNotFoundException();
        //Put elements we need to delete in an array list
        List<String> queryList = new ArrayList<>(queryResults.size());
        for (JsonElement el: queryResults) {
            queryList.add(el.toString());
        }
        // Calculate location from looping through all values. Unfortunately there is no better way to do this
        JsonArray allValues = get(sheetName);
        int count = 1;
        List<Integer> deleteLocations = new ArrayList<>(queryList.size());
        for (JsonElement el: allValues) {
            if (queryList.contains(el.toString()))
                deleteLocations.add(count);
            count +=1;
        }
        this.batchRequestUtility.addDeleteRangeRequest(this.gSheets.get(sheetName).getID(), deleteLocations);
        return this.batchRequestUtility.executeBatch();
    }*/


    private JsonArray addClassProperty(String tableName, JsonArray data) throws ResourceNotFoundException {
        // Interface implementations are placed in this package
        String thisPackage = this.getClass().getPackageName();
        String domainPackage = thisPackage.substring(0, thisPackage.lastIndexOf('.') +1) + "domain";
        Class om;
        try {
            // If Class exists
            om = Class.forName(domainPackage + "." + tableName);
        } catch ( ClassNotFoundException e) {
            throw new ResourceNotFoundException("Unable to find a class by package: " + domainPackage + "." + tableName);
        }
        //Add @class property from returned values
        for (JsonElement el: data ) {
            JsonObject obj = el.getAsJsonObject();
            obj.addProperty("@class", om.getCanonicalName());
        }
        return data;
    }

    public String getLastInsertedRange() {
        return getLastInsertedRange;
    }

}
