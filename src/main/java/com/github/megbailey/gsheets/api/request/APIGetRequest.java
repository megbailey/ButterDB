package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.List;

public class APIGetRequest extends APIRequest {

    public APIGetRequest(String spreadsheetID, Sheets sheetsService)  {
        super(spreadsheetID, sheetsService);
    }

    protected Sheets.Spreadsheets.Get getSpreadsheetRequest() throws IOException{
        Sheets.Spreadsheets.Get request = this.getSheetsService().spreadsheets().get(this.getSpreadsheetID());
        return request;
    }

    public List<List<Object>> getData(String sheetName, String range) throws IOException {
        ValueRange response = this.getSheetsService().spreadsheets().values()
                .get(this.getSpreadsheetID(), sheetName + "!" + range)
                .execute();
        return response.getValues();
    }
}
