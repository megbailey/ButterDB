package com.github.megbailey.google.api.request;

import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.api.GAuthentication;
import com.github.megbailey.google.exception.InvalidInsertionException;
import com.github.megbailey.google.exception.InvalidUpdateException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIRequestUtility extends APIRequest {

    public APIRequestUtility(GAuthentication gAuthentication) {
        super(gAuthentication);
    }

    protected Sheets.Spreadsheets.Get getSpreadsheetRequest() throws IOException{
        Sheets.Spreadsheets.Get request = this.getSheetsService().spreadsheets().get(this.getSpreadsheetID());
        return request;
    }

    public List<Sheet> getSpreadsheetSheets() throws IOException {
        Spreadsheet response = this.getSpreadsheetRequest().execute();
        return response.getSheets();
    }

    public SpreadsheetProperties getSpreadsheetProperties() throws IOException {
        Spreadsheet response = this.getSpreadsheetRequest().execute();
        return response.getProperties();
    }


    /*
    Grab data from a given sheet and cell range.
    */
    public List<List<Object>> getData(String sheetName, String cellRange) throws IOException {
        Sheets.Spreadsheets.Values.Get request = this.getSheetsService().spreadsheets().values()
                .get(this.getSpreadsheetID(), sheetName + "!" + cellRange);
        ValueRange response =  request.execute();
        List<List<Object>> values = response.getValues();
        return values;
    }


    /*
        Create a request to update data in a given sheet.
    */
    protected Sheets.Spreadsheets.Values.Update update(String sheetName, String cellRange, List<List<Object>> data)
            throws IOException {
        ValueRange requestBody = new ValueRange()
                .setRange(sheetName + "!"  + cellRange)
                .setValues(data);

        Sheets.Spreadsheets.Values.Update request =
                this.getSheetsService().spreadsheets().values().update(this.getSpreadsheetID(), cellRange, requestBody);
        request.setValueInputOption("USER_ENTERED");
        return request;
    }


    /*
        Update a range with a given row of data and a sheet name.
    */
    public void updateRow(String sheetName, String cellRange, List<Object> data) throws InvalidUpdateException {
        List<List<Object>> dataList = new ArrayList<>();
        //single row
        dataList.add(data);
        try {
            this.update(sheetName, cellRange, dataList).execute();
        } catch ( IOException e ) {
            e.printStackTrace();
            throw new InvalidUpdateException();
        }
    }


    /*
        Create a request to add data to a sheet.
    */
    protected Sheets.Spreadsheets.Values.Append append( String sheetName, String cellRange, List<List<Object>> data )
            throws IOException {
        ValueRange requestBody = new ValueRange()
                .setMajorDimension( "ROWS" )
                .setValues( data );
        String valueInputOption = "RAW"; //OPTIONS: RAW or USER_ENTERED
        Sheets.Spreadsheets.Values.Append request = this.getSheetsService().spreadsheets().values()
                .append(this.getSpreadsheetID(), sheetName + "!" + cellRange, requestBody)
                .setValueInputOption(valueInputOption);
        return request;
    }

    /*
        Add a row of data to a sheet.
    */
    public ObjectModel appendRow(String sheetName, String cellRange, ObjectModel object)
            throws InvalidInsertionException {
        ArrayList data = new ArrayList<>( 1 );
        data.add( object.listValues() );
        try {
            AppendValuesResponse result = this.append( sheetName, cellRange, data ).execute();
            return object;
        } catch (IOException e) {
            throw new InvalidInsertionException();
        }
    }

}
