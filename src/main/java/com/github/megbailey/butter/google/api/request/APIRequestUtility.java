package com.github.megbailey.butter.google.api.request;

import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIRequestUtility extends APIRequest {
    private static Logger logger = LogManager.getLogger(APIRequestUtility.class.getName());


    public APIRequestUtility(GAuthentication gAuthentication) {
        super(gAuthentication);
    }

    private Sheets.Spreadsheets.Get genGetSheetRequest() throws IOException{
        Sheets.Spreadsheets.Get request = this.getSheetsService().spreadsheets().get(this.getSpreadsheetID());
        return request;
    }

    public List<Sheet> getSpreadsheetSheets() throws IOException {
        Spreadsheet response = this.genGetSheetRequest().execute();
        return response.getSheets();
    }

    public SpreadsheetProperties getSpreadsheetProperties() throws IOException {
        Spreadsheet response =  this.genGetSheetRequest().execute();
        return response.getProperties();
    }


    private Sheets.Spreadsheets.Values.Get genGetRangeRequest(String sheetName, String cellRange) throws IOException {
            Sheets.Spreadsheets.Values.Get request = this.getSheetsService().spreadsheets().values()
                    .get(this.getSpreadsheetID(), sheetName + "!" + cellRange);
        return request;
    }

    /**
        Grab data from a given sheet and cell range.
        @param sheetName the sheet
        @param cellRange the range of cells to retrieve (e.g. A16:D16)
        @return List<List<Object>> - A list of rows and cell values
    */
    public List<List<Object>> getData(String sheetName, String cellRange) throws BadRequestException {
        try {
            ValueRange response = this.genGetRangeRequest(sheetName, cellRange).execute();
            List<List<Object>> values = response.getValues();
            logger.info("Successfully retrieved information");
            return values;
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

    /**
        Create a request to update data in a given sheet.
    */
    private Sheets.Spreadsheets.Values.Update genUpdateRequest(String sheetName, String cellRange, List<List<Object>> data)
            throws IOException {
        String rangeWithSheet = sheetName + "!"  + cellRange;
        ValueRange requestBody = new ValueRange()
                .setRange(sheetName + "!"  + cellRange)
                .setValues(data);

        Sheets.Spreadsheets.Values.Update request = this.getSheetsService()
                .spreadsheets()
                .values()
                .update(this.getSpreadsheetID(), rangeWithSheet, requestBody);
        request.setValueInputOption("USER_ENTERED");
        return request;
    }


    /*
        Update a range with a given row of data and a sheet name.
    */
    public void update(String sheetName, String cellRange, List<List<Object>> dataList) throws BadRequestException {
        try {
            this.genUpdateRequest(sheetName, cellRange, dataList).execute();
            logger.info("Successfully updated range");
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }


    /*
        Create a request to add data to a sheet.
        Docs: https://developers.google.com/sheets/api/reference/rest/v4/spreadsheets.values/append
    */
    private Sheets.Spreadsheets.Values.Append genAppendRequest( String sheetName, String cellRange, List<List<Object>> data )
            throws IOException {
        ValueRange requestBody = new ValueRange()
                .setMajorDimension( "ROWS" )
                .setValues( data );
        String valueInputOption = "USER_ENTERED"; //OPTIONS: RAW or USER_ENTERED
        Sheets.Spreadsheets.Values.Append request = this.getSheetsService().spreadsheets().values()
                .append(this.getSpreadsheetID(), sheetName + "!" + cellRange, requestBody)
                .setValueInputOption(valueInputOption);
        return request;
    }

    /*
        Add row of data to a sheet.
        Docs: https://developers.google.com/sheets/api/reference/rest/v4/spreadsheets.values/append
        @param String sheetName - the sheet
        @param String cellRange - the range of cells to append to. Almost always the first cell in the sheet unless it has multiple tables
        @return List<DataModel> - A list of object models that was appended
    */

    public List<List<Object>> append(String sheetName, String cellRange, List<List<Object>> dataToSend) throws BadRequestException {
        try {
            AppendValuesResponse result = this.genAppendRequest( sheetName, cellRange, dataToSend ).execute();
            logger.info("Successfully appended data");
            return dataToSend;
        } catch (IOException e) {
            throw new BadRequestException();
        }
    }

}
