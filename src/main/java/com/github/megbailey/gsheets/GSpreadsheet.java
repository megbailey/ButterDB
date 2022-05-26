package com.github.megbailey.gsheets;

import java.util.HashMap;

public class GSpreadsheet {
    private final String gSpreadsheetID;
    private HashMap<String, GSheet> gSheets; //A spreadsheet contains a list of sheets which can be found by name

    public GSpreadsheet(String spreadsheetID) {
        this.gSpreadsheetID = spreadsheetID;
    }


    public String getGSpreadsheetID() {
        return gSpreadsheetID;
    }

    public HashMap<String, GSheet> getGSheets() {
        return gSheets;
    }

    public void setGSheets(HashMap<String, GSheet> sheets) {
        this.gSheets = sheets;
    }

    public GSheet getGSheet(String sheetName) {
        if (this.gSheets.containsKey(sheetName))
            return this.gSheets.get(sheetName);
        else
            return null;
    }

    public Integer getGSheetID(String sheetName) {
        if (this.gSheets.containsKey(sheetName))
            return this.gSheets.get(sheetName).getID();
        else
            return null;
    }

}
