package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIUpdateRequest extends APIRequest {

    public APIUpdateRequest(String spreadsheetID, Sheets sheetsService)  {
        super(spreadsheetID, sheetsService);
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
}
