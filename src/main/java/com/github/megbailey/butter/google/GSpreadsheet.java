
package com.github.megbailey.butter.google;

import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.api.APIBatchRequestUtility;
import com.github.megbailey.butter.google.api.APIRequestUtility;
import com.github.megbailey.butter.google.api.APIVisualizationQueryUtility;
import com.github.megbailey.butter.google.exception.BadResponse;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
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

    /*
     * Create & Delete a sheet
     */
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


    /*
     * Get data from a sheet
     */
    public List<List<Object>> getWithRange(String sheetName, String cellRange) throws BadRequestException, ResourceNotFoundException {
        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        return this.regularRequestUtility.getData(sheetName, cellRange);
    }

    public JsonArray getAllRows(String sheetName) throws GAccessException, ResourceNotFoundException, BadResponse, IOException {
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        GSheet gSheet = this.gSheets.get( sheetName );
        String gVizQuery = this.buildGVizSelect( gSheet, new String[0] );
        JsonArray results = this.gVizRequestUtility.executeGVizQuery(gSheet, gVizQuery);
        return addClassProperty(sheetName, results);
    }

    public JsonArray findRows(String sheetName, String[] columnConstraints) throws GAccessException, ResourceNotFoundException, BadResponse, IOException {
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        GSheet gSheet = this.gSheets.get( sheetName );
        String gVizQuery = this.buildGVizSelect( gSheet, new String[0] );
        gVizQuery += " where " + this.buildGVizWhereFragment(gSheet, columnConstraints);
        JsonArray results = this.gVizRequestUtility.executeGVizQuery(gSheet, gVizQuery);
        return addClassProperty(sheetName, results);
    }

    /*
     * Mutate data in a Sheet
     */
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

    public boolean deleteRow(String sheetName, String[] constraint)
            throws GAccessException, ResourceNotFoundException, IOException, BadResponse {
        if ( !this.gSheets.containsKey(sheetName) ) {
            throw new ResourceNotFoundException();
        }

        GSheet gsheet = this.gSheets.get(sheetName);

        // Builds html encoded strings and transforms instances of column labels into the column IDs
        String locateQuery = this.buildGVizSelect(gsheet, null);
        locateQuery += " where " + this.buildGVizWhereFragment(gsheet, constraint);

        JsonArray rowsToDelete = this.gVizRequestUtility.executeGVizQuery(gsheet, locateQuery);

        /* Calculate row location from looping through all values and locating the row(s) to be deleted
         * I don't know of a better way to do this atm because update is done via row index.
         * https://developers.google.com/sheets/api/samples/rowcolumn#delete-rows-columns
         */
        JsonArray rowsToSearch = this.getAllRows( sheetName );
        List<Integer> deleteRowsIndex = new ArrayList<>(1);

        for (int i = 0; i < rowsToSearch.size(); i++) {
            JsonObject curObj = rowsToSearch.get(i).getAsJsonObject();

            for ( JsonElement elToDelete: rowsToDelete ) {
                JsonObject objToDelete = elToDelete.getAsJsonObject();
                if ( objToDelete.get("id").equals( curObj.get("id")) ){
                    // one row offset for columns
                    deleteRowsIndex.add(i + 1);
                }
            }
        }

        this.batchRequestUtility.addDeleteRangeRequest(this.gSheets.get(sheetName).getID(), deleteRowsIndex);
        BatchUpdateSpreadsheetResponse response = this.batchRequestUtility.executeBatch();
        return response.getReplies().isEmpty();
    }


    /*
     * Helper functions
     */
    private String buildGVizSelect(GSheet gsheet, String[] columnsToSelect) throws ResourceNotFoundException {
        StringBuilder query = new StringBuilder("select ");
        if ( columnsToSelect == null || columnsToSelect.length == 0 ) {
            // Include all columns
            query.append(String.join("%2C", gsheet.getColumnIDs()));
        } else {
            // Include only listed fields
            for (String columnLabel :columnsToSelect ) {
                query.append(gsheet.getColumnID(columnLabel)).append("%2C");
            }
        }
        return query.toString();
    }

    private String buildGVizWhereFragment(GSheet gsheet, String [] constraint) {
        String[] transformedConstraints = new String[constraint.length];
        for (int i = 0; i < constraint.length; i++) {
            if (gsheet.getColumnMap().containsKey(constraint[i])) {
                transformedConstraints[i] = gsheet.getColumnMap().get(constraint[i]);
            } else {
                transformedConstraints[i] = constraint[i];
            }
        }

        if ( constraint.length > 0 ) {
            return String.join("", transformedConstraints);
        }
        return "";
    }

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
