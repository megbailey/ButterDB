package com.github.megbailey.gsheets.api;

import com.github.megbailey.gsheets.api.request.APIBatchRequestService;
import com.github.megbailey.gsheets.api.request.APIRequestService;
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
    private final APIRequestService regularService;
    private final APIBatchRequestService batchService;
    private HashMap<String, GSheet> sheets; //A spreadsheet contains a list of sheets which can be found by name

    public GSpreadsheet(String spreadsheetID) throws IOException, GeneralSecurityException {
        Sheets sheetsService = GAuthentication.authenticateServiceAccount();
        this.regularService = APIRequestService.getInstance(spreadsheetID, sheetsService);
        this.batchService = APIBatchRequestService.getInstance(spreadsheetID, sheetsService);
        this.sheets = new HashMap<>();

        List<Sheet> existingSheets = this.regularService.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet:existingSheets) {
            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();
            this.sheets.put( sheetName, new GSheet( this, sheetName, sheetID ));
        }
    }

    public HashMap<String, GSheet> getGSheets() { return this.sheets; }

    public APIRequestService getRegularService() { return this.regularService; }

    public APIBatchRequestService getBatchService() { return this.batchService; }

    public boolean createSheet(String sheetName) throws IOException, RuntimeException {
        //Check if sheet already exists
        if ( !this.sheets.containsKey(sheetName) ) {
            Integer sheetID = this.batchService.createSheet(sheetName);
            this.batchService.executeBatch();
            //Add the new sheet to our cache (map) of sheets
            this.sheets.put( sheetName, new GSheet( this, sheetName, sheetID) );
            return true;
        }
        return false;
    }

    public boolean deleteSheet(String sheetName) throws IOException {
        if ( this.sheets.containsKey(sheetName) )  {
            this.batchService.deleteSheet( this.sheets.get(sheetName).getID() );
            this.batchService.executeBatch();
            //Remove the sheet from our cache
            this.sheets.remove(sheetName);
            return true;
        }
        return false;
    }

    public static void main(String [] args) {


        try {
            GSpreadsheet spreadsheet = new GSpreadsheet("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");

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
                data = gSheet.getData("$A1:$C1");
                System.out.println(gSheet.getName() + ": " + data);
            }

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
