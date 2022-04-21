package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class APIBatchUpdateRequest extends APIBatchRequest {

    public APIBatchUpdateRequest(String spreadsheetID, Sheets sheetService)  {
        super(spreadsheetID, sheetService);
    }

    protected AddSheetRequest createSheetRequest(String sheetName) {
        Integer randomId = new Random().nextInt(Integer.MAX_VALUE - 1000000000) + 1000000000;

        SheetProperties properties = new SheetProperties()
                .setSheetId(randomId)
                .setTitle(sheetName);

        AddSheetRequest addSheetRequest = new AddSheetRequest().setProperties(properties);
        return addSheetRequest;
    }

    protected DeleteSheetRequest deleteSheetRequest(Integer sheetID)  {
        DeleteSheetRequest deleteSheetRequest = new DeleteSheetRequest()
                .setSheetId(sheetID);
        return deleteSheetRequest;
    }

    //NamedRangeRequest
}
