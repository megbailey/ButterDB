package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;

abstract class APIBatchRequest extends APIRequest {

    public APIBatchRequest(String spreadsheetID, Sheets sheetService)  {
        super(spreadsheetID, sheetService);
    }

}
