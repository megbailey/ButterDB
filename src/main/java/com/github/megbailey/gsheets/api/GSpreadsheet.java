package com.github.megbailey.gsheets.api;

import com.github.megbailey.gsheets.api.request.APIRequestController;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class GSpreadsheet {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final APIRequestController REQUEST_CONTROLLER;
    private HashMap<String, GSheet> sheets; //A spreadsheet contains a list of sheets which can be found by name


    /* TODO working list:
        - Add class logger and develop better printouts
        - update Spreadsheet properties
        - update sheet properties -> like a rename or coloring
        - set data in a given sheet
        - set column values in a given sheet
        - use JSONFactory to parse json
     */

    public GSpreadsheet(String spreadsheetID) throws IOException, GeneralSecurityException {
        Sheets sheetsService = GAuthentication.authenticateServiceAccount();
        this.REQUEST_CONTROLLER = APIRequestController.getInstance(spreadsheetID, sheetsService);
        this.sheets = new HashMap<>();

        List<Sheet> existingSheets = this.REQUEST_CONTROLLER.getSpreadsheetSheets();
        SheetProperties properties; String sheetTitle; Integer sheetID;

        // Add any existing sheets to our map of sheets -> Parse as JSON to avoid calling the Sheets API
        for (Sheet sheet:existingSheets) {
            properties = sheet.getProperties();
            sheetTitle = properties.getTitle();
            sheetID = properties.getSheetId();
            this.sheets.put( sheetTitle, new GSheet( this.REQUEST_CONTROLLER, sheetTitle, sheetID ) );
        }
    }

    public HashMap<String, GSheet> getGSheets() {
        return this.sheets;
    }


    private boolean createSheet(String sheetName) throws IOException, RuntimeException {
        //Check if sheet already exists
        if ( !this.sheets.containsKey(sheetName) ) {
            Integer sheetID = this.REQUEST_CONTROLLER.addCreateSheetToBatch(sheetName);
            this.REQUEST_CONTROLLER.executeBatch();
            //Add the new sheet to our cache (map) of sheets
            this.sheets.put( sheetName, new GSheet(this.REQUEST_CONTROLLER, sheetName, sheetID) );
            return true;
        }
        return false;
    }


    private boolean deleteSheet(String sheetName) throws IOException {
        if ( this.sheets.containsKey(sheetName) )  {
            this.REQUEST_CONTROLLER.addDeleteSheetToBatch( this.sheets.get(sheetName).getID() );
            this.REQUEST_CONTROLLER.executeBatch();
            //Remove the sheet from our cache
            this.sheets.remove(sheetName);
            return true;
        }
        return false;
    }

    public static void main(String [] args) {


        try {
            GSpreadsheet spreadsheet = new GSpreadsheet("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");

            //spreadsheet.createSheet("newSheet");
            //List<Sheet> sheets = spreadsheet.getSheets();
            //System.out.println(GSON.toJson(sheets));
            //spreadsheet.deleteSheet("chase");


            HashMap<String, GSheet> gSheets = spreadsheet.getGSheets();
            GSheet gSheet;
            Iterator iterator  = gSheets.keySet().iterator();
            List<List<Object>> data;
            while(iterator.hasNext()) {
                gSheet = gSheets.get(iterator.next());
                data = gSheet.getData("$A1:$A5");
                System.out.println(gSheet.getName() + ": " + data);
            }

            //List<List<Object>> response = spreadsheet.getData("Sheet1!A1:E1");
            //System.out.println(GSON.toJson(response));
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("There was a problem accessing the spreadsheet");
            e.printStackTrace();
        }

    }
}
