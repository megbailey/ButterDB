package com.github.megbailey.gsheets.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.*;

public class GSpreadsheet {
    private static final String APPLICATION_NAME = "Google Sheets as a SQL Database";
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String spreadsheetID;
    private Sheets sheetsService;
    private HashMap<String, GSheets> sheets; //A spreadsheet contains a list of sheets which can be found by name


    /* TODO working list:
        - Add class logger and develop better printouts
        - update Spreadsheet properties
        - update sheet properties -> like a rename or coloring
        - set data in a given sheet
        - set column values in a given sheet
        - use JSONFactory to parse json
     */

    public GSpreadsheet(String spreadsheetID) throws IOException {
        this.spreadsheetID = spreadsheetID;
        this.authenticate();
        this.sheets = new HashMap<>();

        List<Sheet> existingSheets = getSheets();
        JsonObject jsonObject; String sheetTitle; Integer sheetID;

        // Add any existing sheets to our map of sheets -> Parse as JSON to avoid calling the Sheets API
        for (Sheet sheet:existingSheets) {
            jsonObject = new Gson().fromJson(GSON.toJson(sheet), JsonObject.class);
            sheetTitle = jsonObject.getAsJsonObject("properties").get("title").getAsString();
            sheetID = jsonObject.getAsJsonObject("properties").get("sheetId").getAsInt();
            this.sheets.put( sheetTitle, new GSheets( this.sheetsService, sheetTitle, sheetID ) );
        }
    }

    public String getSpreadsheetID() {
        return this.spreadsheetID;
    }

    public Sheets getSheetsService() {
        return this.sheetsService;
    }

    private void authenticate() {
        try {
            NetHttpTransport HTTPTransport = GoogleNetHttpTransport.newTrustedTransport();
            InputStream credentialsStream = GSheets.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
            GoogleCredentials googleCredentials;

            googleCredentials = GoogleCredentials.fromStream(credentialsStream).createScoped(SCOPES);
            this.sheetsService = new Sheets.Builder(HTTPTransport, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch (IOException | GeneralSecurityException e) {
            System.out.println("Trouble authenticating.. please see stack trace and check your Google sheet credentials");
            e.printStackTrace();
        }
    }

    private Spreadsheet getSpreadsheet() throws IOException {
        Sheets.Spreadsheets.Get request = this.sheetsService.spreadsheets().get(this.spreadsheetID);
        Spreadsheet response = request.execute();
        return response;
    }

    /* Grab the properties of the spreadsheet
     * @return SpreadsheetProperties A JSON object of the spreadsheet properties
     */
    private SpreadsheetProperties getSpreadsheetProperties() throws IOException {
        Sheets.Spreadsheets.Get request = this.sheetsService.spreadsheets().get(this.spreadsheetID);
        Spreadsheet response = request.execute();
        return response.getProperties();
    }


    public HashMap<String, GSheets> getGSheets() {
        return this.sheets;
    }

    /* Grab all current lists in the spreadsheet
     * @return List<Sheets> A list of sheet objects in the spreadsheet
     */
    private List<Sheet> getSheets() throws IOException {
            Sheets.Spreadsheets.Get request = this.sheetsService.spreadsheets().get(this.spreadsheetID);
            Spreadsheet response = request.execute();
            return response.getSheets();
    }

    /* Create a new sheet within the spreadsheet with a given index
     * @param String a name for the new sheet
     * @param int an index for the new sheet
     * @return void
     */
    private void createSheet(String sheetName) throws IOException, RuntimeException {

        //Check if sheet already exists
        System.out.println(this.sheets.containsKey(sheetName));

        if ( !this.sheets.containsKey(sheetName) ) {
            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            List<Request> requests = new ArrayList<>();

            //  Create a new randomId
            Integer randomId = new Random().nextInt(Integer.MAX_VALUE - 1000000000) + 1000000000;

            // Create new Sheet properties
            SheetProperties properties = new SheetProperties()
                    .setSheetId(randomId)
                    .setTitle(sheetName);

            // Create the request
            AddSheetRequest addSheetRequest = new AddSheetRequest().setProperties(properties);
            requests.add(new Request().setAddSheet(addSheetRequest));
            requestBody.setRequests(requests);

            Sheets.Spreadsheets.BatchUpdate request =
                    this.sheetsService.spreadsheets().batchUpdate(this.spreadsheetID, requestBody);
            request.execute();

            //Add the new sheet to our cache (map) of sheets
            this.sheets.put(sheetName, new GSheets(this.sheetsService, sheetName, randomId));
        }  else {
            System.out.println("Sheet name already exists. Did not create.");
        }
    }


    private void deleteSheet(String sheetName) throws IOException {

        if ( this.sheets.containsKey(sheetName) )  {
            BatchUpdateSpreadsheetRequest requestBody = new BatchUpdateSpreadsheetRequest();
            List<Request> requests = new ArrayList<>();

            DeleteSheetRequest deleteSheetRequest =  new DeleteSheetRequest()
                    .setSheetId(this.sheets.get(sheetName).getID());
            requests.add(new Request().setDeleteSheet(deleteSheetRequest));
            requestBody.setRequests(requests);

            Sheets.Spreadsheets.BatchUpdate request =
                    this.sheetsService.spreadsheets().batchUpdate(this.spreadsheetID, requestBody);
            request.execute();

            //Remove the sheet from our cache
            this.sheets.remove(sheetName);
        } else {
            System.out.println("Sheet with specified name could not be found. Did not delete.");
        }

    }

    public static void main(String [] args) {


        try {
            GSpreadsheet spreadsheet = new GSpreadsheet("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");

            //spreadsheet.createSheet("newSheet");
            //List<Sheet> sheets = spreadsheet.getSheets();
            //System.out.println(GSON.toJson(sheets));

            spreadsheet.deleteSheet("chase");

            Iterator iterator  = spreadsheet.getGSheets().keySet().iterator();
            while(iterator.hasNext()) {
                System.out.println(iterator.next());
            }
            //SpreadsheetProperties properties = new SpreadsheetProperties().set()

            //List<List<Object>> response = spreadsheet.getData("Sheet1!A1:E1");
            //System.out.println(GSON.toJson(response));
        } catch (IOException e) {
            System.out.println("There was a problem accessing the spreadsheet");
            e.printStackTrace();
        }

    }
}
