package com.github.megbailey.gsheets.api.request.gviz;

import com.github.megbailey.gsheets.api.request.APIRequest;
import com.google.api.services.sheets.v4.Sheets;

public class APIVisualizationQueryFactory extends APIRequest {
    public APIVisualizationQueryFactory(String spreadsheetID, Sheets sheetsService)  {
        super(spreadsheetID, sheetsService);
    }

    /* TODO:
    * - Create GViz GET request given an already formatted query and sheetID
    */
}
