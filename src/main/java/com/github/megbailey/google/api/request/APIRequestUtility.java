package com.github.megbailey.google.api.request;

import com.github.megbailey.google.api.GAuthentication;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIRequestUtility extends APIRequest {
    // Singleton class
    private static APIRequestUtility instance;


    private APIRequestUtility(GAuthentication gAuthentication) {
        super(gAuthentication);
    }

    public static synchronized APIRequestUtility getInstance(GAuthentication gAuthentication) {
        if (instance == null) {
            instance = new APIRequestUtility(gAuthentication);
        }
        return instance;
    }

    protected Sheets.Spreadsheets.Get getSpreadsheetRequest() throws IOException{
        Sheets.Spreadsheets.Get request = this.getSheetsService().spreadsheets().get(this.getSpreadsheetID());
        return request;
    }

    public List<Sheet> getSpreadsheetSheets() throws IOException {
        Spreadsheet response = this.getSpreadsheetRequest().execute();
        return response.getSheets();
    }

    public SpreadsheetProperties getSpreadsheetProperties() throws IOException {
        Spreadsheet response = this.getSpreadsheetRequest().execute();
        return response.getProperties();
    }

    protected Sheets.Spreadsheets.Values.Update updateData(String range, List<List<Object>> values) throws IOException {
        ValueRange requestBody = new ValueRange()
                .setRange(range)
                .setValues(values);

        Sheets.Spreadsheets.Values.Update request =
                this.getSheetsService().spreadsheets().values().update(this.getSpreadsheetID(), range, requestBody);
        request.setValueInputOption("USER_ENTERED");
        return request;
    }


    public void updateData(String sheetName, String range, List<Object> values) throws IOException {
        List<List<Object>> wrappedValues = new ArrayList<>();
        wrappedValues.add(values);
        this.updateData(sheetName + "!"  + range, wrappedValues).execute();
    }


    public List<List<Object>> getData(String sheetName, String range) throws IOException {
        Sheets.Spreadsheets.Values.Get request = this.getSheetsService().spreadsheets().values()
            .get(this.getSpreadsheetID(), sheetName + "!" + range);
        ValueRange response =  request.execute();
        return response.getValues();
    }

}
