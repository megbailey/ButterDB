package com.github.megbailey.gsheets.api.request;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class APIBatchRequest extends APIRequest {
    private List<Request> requests;

    public APIBatchRequest(String spreadsheetID, Sheets sheetService)  {
        super(spreadsheetID, sheetService);
        this.requests = new ArrayList<>();
    }

    public boolean executeBatchRequests() throws IOException {
        if ( !requests.isEmpty() ) {
            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            requestBody.setRequests(requests);
            Sheets.Spreadsheets.BatchUpdate request =
                    this.getSheetsService().spreadsheets().batchUpdate(this.getSpreadsheetID(), requestBody);
            request.execute();
            this.requests.clear();
            return true;
        }
        return false;
    }

    protected Integer createSheetRequest(String sheetName) {
        Integer randomId = new Random().nextInt(Integer.MAX_VALUE - 1000000000) + 1000000000;

        SheetProperties properties = new SheetProperties()
                .setSheetId(randomId)
                .setTitle(sheetName);

        AddSheetRequest addSheetRequest = new AddSheetRequest().setProperties(properties);
        requests.add(new Request().setAddSheet(addSheetRequest));
        return randomId;
    }

    protected void deleteSheetRequest(Integer sheetID)  {
        DeleteSheetRequest deleteSheetRequest = new DeleteSheetRequest()
                .setSheetId(sheetID);
        requests.add(new Request().setDeleteSheet(deleteSheetRequest));
    }

    //NamedRangeRequest
}
