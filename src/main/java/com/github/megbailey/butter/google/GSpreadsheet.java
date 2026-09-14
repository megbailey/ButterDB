
package com.github.megbailey.butter.google;

import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.api.APIBatchRequestUtility;
import com.github.megbailey.butter.google.api.APIRequestUtility;
import com.github.megbailey.butter.google.api.APIVisualizationQueryUtility;
import com.github.megbailey.butter.exception.SystemErrorException;
import com.github.megbailey.butter.exception.GoggleAccessException;
import com.github.megbailey.butter.exception.BadRequestException;
import com.github.megbailey.butter.exception.ResourceNotFoundException;
import com.github.megbailey.butter.util.ColumnLetters;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.*;

public class GSpreadsheet {

    // A list of sheets which can be found by name
    private final Map<String, Integer> gSheets;
    // Utilities to help perform the API calls to google
    private final APIRequestUtility regularRequestUtility;
    private final APIBatchRequestUtility batchRequestUtility;
    private final APIVisualizationQueryUtility gVizRequestUtility;
    // The cell range of the last inserted value
    private String getLastInsertedRange;

    public final HashBiMap<Integer, String> cellIDsMap = HashBiMap.create();



    public GSpreadsheet(GAuthentication gAuthentication) throws IOException {
        this.regularRequestUtility = new APIRequestUtility(gAuthentication);
        this.batchRequestUtility = new APIBatchRequestUtility(gAuthentication);
        this.gVizRequestUtility = new APIVisualizationQueryUtility(gAuthentication);

        this.gSheets = new HashMap<>();
        this.syncRepositories();
        this.cellIDsMap.putAll(ColumnLetters.defaultMap());
    }

    private void syncRepositories() throws IOException {
        List<Sheet> sheets = this.regularRequestUtility.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet: sheets) {

            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();

            this.gSheets.put(sheetName, sheetID);

        }
    }

    /*
     * Create & Delete a sheet
     */
    public Integer firstOrNewSheet(String sheetName) throws IOException, BadRequestException {
        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey(sheetName) ) {
            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
            this.batchRequestUtility.executeBatch();
            // Add the new sheet to our cache/map of sheets
            gSheets.put(sheetName, sheetID);
        }
        return this.gSheets.get(sheetName);

    }

    public Integer deleteGSheet(String sheetName) throws IOException, ResourceNotFoundException {

        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        Integer removeSheetID =  this.gSheets.get(sheetName);
        this.batchRequestUtility.addDeleteSheetRequest( removeSheetID );
        this.batchRequestUtility.executeBatch();

        //Remove the sheet from our cache
        gSheets.remove( sheetName );
        return removeSheetID;

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

    public JsonArray getAll( String sheetName, Set<String> cellIDs ) throws GoggleAccessException, ResourceNotFoundException, SystemErrorException, IOException {
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        String allRowsQuery = "select " + String.join("%2C", cellIDs );
        Integer sheetID = this.gSheets.get( sheetName );
        return this.gVizRequestUtility.executeGVizQuery(sheetID, allRowsQuery);
    }

    public JsonArray findRows(String sheetName, String gVizQuery) throws GoggleAccessException, ResourceNotFoundException, SystemErrorException, IOException {
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        Integer sheetID = this.gSheets.get( sheetName );
        return this.gVizRequestUtility.executeGVizQuery(sheetID, gVizQuery);
    }

    /*
     * Mutate data in a Sheet
     */
    public void updateRow(String sheetName, List<Object> row) throws ResourceNotFoundException, BadRequestException {
        //Check if sheet already exists to avoid an API call
        if ( !this.gSheets.containsKey( sheetName ) ) {
            throw new ResourceNotFoundException();
        }

        List<List<Object>> dataToSend = new ArrayList<>(1);
        dataToSend.add(row);
        String lastColumnCellID = this.cellIDsMap.get(Math.max(row.size() - 1, 0));
        this.regularRequestUtility.update(sheetName, "A1:" + lastColumnCellID + "1", dataToSend);
    }

    /** Update an arbitrary data range (e.g. A5:D5) with a single row of values. */
    public void updateDataRow(String sheetName, String range, List<Object> row)
            throws ResourceNotFoundException, BadRequestException {
        if (!this.gSheets.containsKey(sheetName)) {
            throw new ResourceNotFoundException();
        }
        List<List<Object>> dataToSend = new ArrayList<>(1);
        dataToSend.add(row);
        this.regularRequestUtility.update(sheetName, range, dataToSend);
    }

    /**
     * Delete sheet rows by 0-based dimension indices (header is row 0).
     * Indices are sorted descending before deletion so later rows do not shift.
     */
    public void deleteRowsByIndex(String sheetName, List<Integer> zeroBasedIncludingHeader)
            throws ResourceNotFoundException, IOException {
        if (!this.gSheets.containsKey(sheetName)) {
            throw new ResourceNotFoundException();
        }
        if (zeroBasedIncludingHeader == null || zeroBasedIncludingHeader.isEmpty()) {
            return;
        }
        Integer sheetID = this.gSheets.get(sheetName);
        List<Integer> sorted = new ArrayList<>(zeroBasedIncludingHeader);
        sorted.sort(Comparator.reverseOrder());
        this.batchRequestUtility.addDeleteRangeRequest(sheetID, sorted);
        this.batchRequestUtility.executeBatch();
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

    public boolean deleteRow(String sheetName, Set<String> cellIDs, String gVizWhere)
            throws GoggleAccessException, ResourceNotFoundException, IOException, SystemErrorException {
        if ( !this.gSheets.containsKey(sheetName) ) {
            throw new ResourceNotFoundException();
        }

        Integer sheetID = this.gSheets.get(sheetName);

        JsonArray rowsToDelete = this.gVizRequestUtility.executeGVizQuery(
                sheetID,
                this.gVizSelectColumnsFragment(cellIDs) + " " + gVizWhere
        );

        /* Calculate row location from looping through all values and locating the row(s) to be deleted
         * I don't know of a better way to do this atm because update is done via row index.
         * https://developers.google.com/sheets/api/samples/rowcolumn#delete-rows-columns
         */
        JsonArray rowsToSearch = this.getAll( sheetName, cellIDs );
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


        this.batchRequestUtility.addDeleteRangeRequest(sheetID, deleteRowsIndex);
        BatchUpdateSpreadsheetResponse response = this.batchRequestUtility.executeBatch();
        return response.getReplies().isEmpty();
    }



    private String gVizSelectColumnsFragment(Set<String> columnIDs ) {
        return "select " + String.join(",", columnIDs);
    }
}
