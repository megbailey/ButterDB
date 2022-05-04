package com.github.megbailey.gsheets.api;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GAuthentication {
    private static final String APPLICATION_NAME = "Google Sheets as a SQL Database";
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private String spreadsheetID;
    private GoogleCredentials googleCredentials;
    private Sheets sheetsService;

    public GAuthentication(String spreadsheetID) {
        this.spreadsheetID = spreadsheetID;
    }

    public void authenticateWithServiceAccount() throws  IOException, GeneralSecurityException {
        NetHttpTransport HTTPTransport = GoogleNetHttpTransport.newTrustedTransport();
        InputStream credentialsStream = GSpreadsheet.class.getResourceAsStream(CREDENTIALS_FILE_PATH);

        this.googleCredentials = GoogleCredentials.fromStream(credentialsStream).createScoped(SCOPES);
        this.sheetsService = new Sheets.Builder(HTTPTransport, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Sheets getSheetsService() {
        return this.sheetsService;
    }

    public String getAccessToken() throws IOException {
        this.googleCredentials.refreshIfExpired();
        return this.googleCredentials.getAccessToken().getTokenValue();
    }

    public String getSpreadsheetID() {
        return spreadsheetID;
    }
}
