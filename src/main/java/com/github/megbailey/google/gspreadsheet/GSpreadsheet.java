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
        List<Sheet> sheets = this.regularRequestUtility.getSpreadsheetSheets();

        // Add any existing sheets to our map of sheets
        for (Sheet sheet: sheets) {
            SheetProperties properties = sheet.getProperties();
            String sheetName = properties.getTitle();
            Integer sheetID = properties.getSheetId();
            gSheets.put( sheetName, new GSheet().setName(sheetName).setID(sheetID) );
        }
        this.gSheets = gSheets;
    }

    public HashMap<String, GSheet> getGSheets() {
        return gSheets;
    }

    public GSheet getGSheet(String sheetName) {
        if (this.gSheets.containsKey(sheetName))
            return this.gSheets.get(sheetName);
        else
            return null;
    }

//    public Integer createGSheet(String sheetName) throws IOException {
//        HashMap gSheets = this.gSpreadsheet.getGSheets();
//
//        //Check if sheet already exists to avoid an API call
//        if ( !gSheets.containsKey(sheetName) ) {
//
//            Integer sheetID = this.batchRequestUtility.addCreateSheetRequest(sheetName);
//            this.batchRequestUtility.executeBatch();
//            GSheet gSheet = GSheet.build()
//                    .setName(sheetName)
//                    .setID(sheetID);
//
//            //Add the new sheet to our cache (map) of sheets
//            gSheets.put( sheetName, gSheet );
//            return sheetID;
//        }
//
//        return null;
//    }
//    public boolean deleteGSheet(String sheetName) throws IOException {
//        HashMap gSheets = this.gSpreadsheet.getGSheets();
//        //Check if sheet already exists to avoid an API call
//        if ( gSheets.containsKey(sheetName) )  {
//
//            this.batchRequestUtility.addDeleteSheetRequest( this.gSpreadsheet.getGSheetID(( sheetName )));
//            this.batchRequestUtility.executeBatch();
//
//            //Remove the sheet from our cache
//            gSheets.remove(sheetName);
//            return true;
//        }
//        return false;
//    }

    public JsonArray executeSelect(String query, String className) throws IOException {
        System.out.println(query);
        if ( this.gSheets.containsKey(className) ) {
            Integer sheetID = this.gSheets.get(className).getID();
            JsonArray ar = this.gVizRequestUtility.executeGVizQuery(query, sheetID);
            System.out.println(ar);
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
