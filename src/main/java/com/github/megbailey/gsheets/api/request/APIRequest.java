package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;

public abstract class APIRequest {
    private final String SPREADSHEET_ID;
    private final Sheets SHEETS_SERVICE;

    public APIRequest(String spreadsheetID, Sheets sheetsService) {
        this.SPREADSHEET_ID = spreadsheetID;
        this.SHEETS_SERVICE = sheetsService;
    }

    protected String getSpreadsheetID() {
        return this.SPREADSHEET_ID;
    }

    protected Sheets getSheetsService() {
        return this.SHEETS_SERVICE;
    }
}
