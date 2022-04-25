package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;

public class APIGetRequestFactory extends APIRequest {

    public APIGetRequestFactory(String spreadsheetID, Sheets sheetsService)  {
        super(spreadsheetID, sheetsService);
    }

    protected Sheets.Spreadsheets.Get getSpreadsheetRequest() throws IOException{
        Sheets.Spreadsheets.Get request = this.getSheetsService().spreadsheets().get(this.getSpreadsheetID());
        return request;
    }

    public Sheets.Spreadsheets.Values.Get getValueRequest(String range) throws IOException {
        Sheets.Spreadsheets.Values.Get request = this.getSheetsService().spreadsheets().values()
                .get(this.getSpreadsheetID(), range);
        return request;
    }
}
