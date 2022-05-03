package com.github.megbailey.gsheets.api.request;

import com.github.megbailey.gsheets.api.GAuthentication;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class APIBatchRequestUtility extends APIRequest {
    private static APIBatchRequestUtility instance;
    private List<Request> requests;

    private APIBatchRequestUtility(GAuthentication gAuthentication)  {
        super(gAuthentication);
        this.requests = new ArrayList<>();
    }

    public static synchronized APIBatchRequestUtility getInstance(GAuthentication gAuthentication) {
        if (instance == null) {
            instance = new APIBatchRequestUtility(gAuthentication);
        }
        return instance;
    }

    public boolean executeBatch() throws IOException {
        if (!requests.isEmpty()) {
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

    public Integer addCreateSheetRequest(String sheetName) {
        Integer randomId = new Random().nextInt(Integer.MAX_VALUE - 1000000000) + 1000000000;

        SheetProperties properties = new SheetProperties()
                .setSheetId(randomId)
                .setTitle(sheetName);

        AddSheetRequest addSheetRequest = new AddSheetRequest().setProperties(properties);
        this.requests.add(new Request().setAddSheet(addSheetRequest));
        return addSheetRequest.getProperties().getSheetId();
    }

    public void addDeleteSheetRequest(Integer sheetID)  {
        DeleteSheetRequest deleteSheetRequest = new DeleteSheetRequest()
                .setSheetId(sheetID);
        this.requests.add(new Request().setDeleteSheet(deleteSheetRequest));
    }

}
