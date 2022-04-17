package com.github.megbailey.gsheets.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GSpreadsheet {
    private static final String APPLICATION_NAME = "Google Sheets as a SQL Database";
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final String REST_SERVICE = "https://sheets.googleapis.com/v4/spreadsheets/";
    protected final String spreadsheetID;
    protected List<GSheets> GSheets; //A spreadsheet contains a list of sheet ids
    protected Sheets sheetsService;


    public GSpreadsheet(String spreadsheetID) {
        this.spreadsheetID = spreadsheetID;
        this.GSheets = new ArrayList<>();
        //TODO: Foreach existing sheet - add it to our list
    }

    public String getSpreadsheetID() {
        return this.spreadsheetID;
    }

    public Sheets getSheetsService() {
        return this.sheetsService;
    }

    private void authenticate() throws IOException, GeneralSecurityException {
        NetHttpTransport HTTPTransport = GoogleNetHttpTransport.newTrustedTransport();
        InputStream credentialsStream = GSheets.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleCredentials googleCredentials;

        if (credentialsStream != null) {
            googleCredentials = GoogleCredentials.fromStream(credentialsStream).createScoped(SCOPES);
            this.sheetsService = new Sheets.Builder(HTTPTransport, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } else {
            throw new IOException("Credential stream is null");
        }
    }

    private SpreadsheetProperties getSpreadsheetProperties() throws IOException {
        Sheets.Spreadsheets.Get request = this.sheetsService.spreadsheets().get(this.spreadsheetID);
        Spreadsheet response = request.execute();
        return response.getProperties();
    }

    private List<Sheet> getSpreadsheetSheets() throws IOException {
        Sheets.Spreadsheets.Get request = this.sheetsService.spreadsheets().get(this.spreadsheetID);
        Spreadsheet response = request.execute();
        return response.getSheets();
    }

    private void addSheet(SheetProperties properties) {

    }

    public static void main(String [] args) throws IOException, GeneralSecurityException {

        GSpreadsheet spreadsheet = new GSpreadsheet("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");
        spreadsheet.authenticate();
        List<Sheet> sheets = spreadsheet.getSpreadsheetSheets();
        System.out.println(GSON.toJson(sheets));

        //List<List<Object>> response = spreadsheet.getData("Sheet1!A1:E1");
        //System.out.println(GSON.toJson(response));
    }
}
