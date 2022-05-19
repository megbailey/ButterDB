package com.github.megbailey.gsheets;

import com.github.megbailey.gsheets.api.GAuthentication;
import com.github.megbailey.gsheets.api.request.APIBatchRequestUtility;
import com.github.megbailey.gsheets.api.request.APIRequestUtility;
import com.github.megbailey.gsheets.api.request.APIVisualizationQueryUtility;

import com.google.api.services.sheets.v4.model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import java.util.logging.Logger;


@Component
public class GSpreadsheetManager {
    private static final Logger logger = Logger.getLogger( GSpreadsheetManager.class.getName() );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private GAuthentication gAuthentication;
    private APIVisualizationQueryUtility gVizRequestUtility;
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;
    private HashMap<String, GSheet> sheets; //A spreadsheet contains a list of sheets which can be found by name

    public GSpreadsheetManager(String spreadsheetID) throws IOException, GeneralSecurityException {
        this.gAuthentication = new GAuthentication(spreadsheetID);
        this.gAuthentication.authenticateWithServiceAccount();

        this.gVizRequestUtility = new APIVisualizationQueryUtility(this.gAuthentication);
        this.regularRequestUtility = APIRequestUtility.getInstance(this.gAuthentication);
        this.batchRequestUtility = APIBatchRequestUtility.getInstance(this.gAuthentication);
        this.sheets = new HashMap<>();

        List<Sheet> existingSheets = this.regularRequestUtility.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet:existingSheets) {
            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();
            this.sheets.put( sheetName, new GSheet( this, sheetName, sheetID ));
        }
    }

    public HashMap<String, GSheet> getGSheets() { return this.sheets; }

    public APIRequestUtility getRegularService() { return this.regularRequestUtility; }

    public APIBatchRequestUtility getBatchService() { return this.batchRequestUtility; }

    public Integer getGSheet(String sheetName) {
        if (this.sheets.containsKey(sheetName))
            return this.sheets.get(sheetName).getID();
        else
            return null;
    }

    public boolean createGSheet(String sheetName) throws IOException, RuntimeException {
        //Check if sheet already exists to avoid an API call
        if ( !this.sheets.containsKey(sheetName) ) {
            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
            this.batchRequestUtility.executeBatch();

            //Add the new sheet to our cache (map) of sheets
            this.sheets.put( sheetName, new GSheet( this, sheetName, sheetID) );
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


    /* query PRE GViz transformation */
    public JsonArray filterGSheet(String query, String sheetName) throws IOException {
        if (this.sheets.containsKey(sheetName)) {
            Integer sheetID = this.sheets.get(sheetName).getID();
            Response response = this.gVizRequestUtility.executeGVizQuery(query, sheetID);

            return this.gVizRequestUtility.parseGVizResponse(response);
        }
        return null;
    }



}
