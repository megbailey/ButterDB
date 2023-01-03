package com.github.megbailey.butter.google.api.request;

import com.github.megbailey.butter.google.api.GAuthentication;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class APIBatchRequestUtility extends APIRequest {
    private static Logger logger = LogManager.getLogger(APIBatchRequestUtility.class.getName());
    private List<Request> requests;

    public APIBatchRequestUtility(GAuthentication gAuthentication)  {
        super(gAuthentication);
        this.requests = new ArrayList<>();
    }


    public boolean executeBatch() throws IOException {
        if (requests.isEmpty()) {
            return false;
        }

        BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
        requestBody.setRequests(requests);
        Sheets.Spreadsheets.BatchUpdate request =
                this.getSheetsService().spreadsheets().batchUpdate(this.getSpreadsheetID(), requestBody);
        BatchUpdateSpreadsheetResponse response = request.execute();
        this.requests.clear();
        logger.info("Executing batch request (" + requests.size() + ")");
        return true;
    }

    public Integer addCreateSheetRequest(String sheetName) {
        Integer randomId = new Random().nextInt(Integer.MAX_VALUE - 1000000000) + 1000000000;

        SheetProperties properties = new SheetProperties()
                .setSheetId(randomId)
                .setTitle(sheetName);

        AddSheetRequest addSheetRequest = new AddSheetRequest().setProperties(properties);
        this.requests.add(new Request().setAddSheet(addSheetRequest));
        logger.info("Added a 'Add Sheet' request to the queue");
        return addSheetRequest.getProperties().getSheetId();
    }

    public void addDeleteSheetRequest(Integer sheetID)  {
        DeleteSheetRequest deleteSheetRequest = new DeleteSheetRequest()
                .setSheetId(sheetID);
        this.requests.add(new Request().setDeleteSheet(deleteSheetRequest));
        logger.info("Added a 'Delete Sheet' request to the queue");
    }

    public void addDeleteRangeRequest(Integer sheetID, List<Integer> rows) {
        for (Integer row: rows) {
            DimensionRange range = new DimensionRange()
                    .setSheetId(sheetID)
                    .setStartIndex(row)
                    .setEndIndex(row+1)
                    .setDimension("ROWS");
            DeleteDimensionRequest deleteDimensionRequest = new DeleteDimensionRequest().setRange(range);
            this.requests.add(new Request().setDeleteDimension(deleteDimensionRequest));
        }
        logger.info("Added (" + rows.size() + ") 'Delete Range' Request(s)");
    }

}
