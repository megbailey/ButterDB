package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIRequestService extends APIRequest {
    // Singleton class
    private static APIRequestService instance;
    // Delegators
    private APIGetRequest getRequestService;
    private APIUpdateRequest updateRequestService;

    private APIRequestService(String spreadsheetID, Sheets sheetsService) {
        super(spreadsheetID, sheetsService);
        this.getRequestService = new APIGetRequest(this.getSpreadsheetID(), this.getSheetsService());
        this.updateRequestService = new APIUpdateRequest(this.getSpreadsheetID(), this.getSheetsService());
    }

    public static synchronized APIRequestService getInstance(String spreadsheetID, Sheets sheetsService) {
        if (instance == null) {
            instance = new APIRequestService(spreadsheetID, sheetsService);
        }
        return instance;
    }

    public List<Sheet> getSpreadsheetSheets() throws IOException {
        Spreadsheet response = this.getRequestService.getSpreadsheetRequest().execute();
        return response.getSheets();
    }

    public SpreadsheetProperties getSpreadsheetProperties() throws IOException {
        Spreadsheet response = this.getRequestService.getSpreadsheetRequest().execute();
        return response.getProperties();
    }

    public List<List<Object>> getData(String sheetName, String range) throws IOException {
        ValueRange response =  this.getRequestService.getValueRequest(sheetName + "!"  + range).execute();
        return response.getValues();
    }

    public void updateData(String sheetName, String range, List<Object> values) throws IOException {
        List<List<Object>> wrappedValues = new ArrayList<>();
        wrappedValues.add(values);
        this.updateRequestService.updateData(sheetName + "!"  + range, wrappedValues).execute();
    }

}
