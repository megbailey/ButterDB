package com.github.megbailey.gsheets.api;

import com.github.megbailey.gsheets.api.request.APIBatchRequestUtility;
import com.github.megbailey.gsheets.api.request.APIRequestUtility;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import java.util.logging.Logger;


public class GSpreadsheet {
    private static final Logger logger = Logger.getLogger( GSpreadsheet.class.getName() );
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private GAuthentication gAuthentication;
    private APIRequestUtility regularRequestUtility;
    private APIBatchRequestUtility batchRequestUtility;
    private HashMap<String, GSheet> sheets; //A spreadsheet contains a list of sheets which can be found by name

    public GSpreadsheet(String spreadsheetID) throws IOException, GeneralSecurityException {
        this.gAuthentication = new GAuthentication(spreadsheetID);
        this.gAuthentication.authenticateWithServiceAccount();

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

    public boolean createSheet(String sheetName) throws IOException, RuntimeException {
        //Check if sheet already exists
        if ( !this.sheets.containsKey(sheetName) ) {
            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
            this.batchRequestUtility.executeBatch();
            //Add the new sheet to our cache (map) of sheets
            this.sheets.put( sheetName, new GSheet( this, sheetName, sheetID) );
            return true;
        }
        return false;
    }

    public boolean deleteSheet(String sheetName) throws IOException {
        if ( this.sheets.containsKey(sheetName) )  {
            this.batchRequestUtility.addDeleteSheetRequest( this.sheets.get(sheetName).getID() );
            this.batchRequestUtility.executeBatch();
            //Remove the sheet from our cache
            this.sheets.remove(sheetName);
            return true;
        }
        return false;
    }

    public static void main(String [] args) {


        try {
            GSpreadsheet spreadsheet = new GSpreadsheet("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");

            /*
            GSheet classSchema = spreadsheet.getGSheets().get("class.schema");
            List<Object> columnNames = new ArrayList<>(5);
            columnNames.add("column1");
            columnNames.add("column2");
            columnNames.add("column3");
            classSchema.updateData("$A1:$C1", columnNames);

            // Sample get data

            HashMap<String, GSheet> gSheets = spreadsheet.getGSheets();
            GSheet gSheet;
            Iterator iterator  = gSheets.keySet().iterator();
            List<List<Object>> data;
            while(iterator.hasNext()) {
                gSheet = gSheets.get(iterator.next());

                //data = gSheet.getData("$A1:$C1");
                //System.out.println(gSheet.getName() + ": " + data);
            }
            */
            // Sample create sheet
            /*
            spreadsheet.createSheet("newSheet");
            List<Sheet> sheets = spreadsheet.getSheets();
            System.out.println(GSON.toJson(sheets));
            */

            // Sample delete sheet
            /*
            spreadsheet.deleteSheet("chase");
            List<Sheet> sheets = spreadsheet.getSheets();
            System.out.println(GSON.toJson(sheets));
            */
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("There was a problem accessing the spreadsheet");
            e.printStackTrace();
        }

    }
}
