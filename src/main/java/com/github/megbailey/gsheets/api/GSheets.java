package com.github.megbailey.gsheets.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonParser;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;

import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GSheets {
    private static final String APPLICATION_NAME = "Google Sheets as a SQL Database";
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String spreadsheetID;
    private Sheets sheetsService;

    public GSheets(String spreadsheetID) {
        this.spreadsheetID = spreadsheetID;
    }

    /**
     * Simple authentication flow for authenticating with Google as a service account
     * @throws IOException - thrown if client_secret.json is not found
     * @throws GeneralSecurityException - thrown if Trusted HTTP Transport cannot be created
     */
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

    public List<List<Object>> getData(String range) throws IOException {
        ValueRange response = this.sheetsService.spreadsheets().values()
                .get(this.spreadsheetID, range)
                .execute();
        return response.getValues();
    }

    public static void main(String [] args) throws IOException, GeneralSecurityException {

        GSheets gSheet = new GSheets("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");
        gSheet.authenticate();

        List<List<Object>> response = gSheet.getData("Sheet1!A1:E1");
        System.out.println(GSON.toJson(response));
    }
}