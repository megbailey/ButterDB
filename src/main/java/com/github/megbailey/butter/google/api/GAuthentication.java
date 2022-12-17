package com.github.megbailey.butter.google.api;

import com.github.megbailey.butter.db.ButterDBService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GAuthentication {
    private static GAuthentication instance;
    private static final String APPLICATION_NAME = "Google Sheets as a SQL Database";
    private static final String CREDENTIALS_FILE_PATH = "/client_secret.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private String spreadsheetID;
    private GoogleCredentials googleCredentials;
    private Sheets sheetsService;


    private GAuthentication(GoogleCredentials credentials) {
        this.googleCredentials = credentials.createScoped(SCOPES);
    }


    public static GAuthentication getInstance(GoogleCredentials credentials) {
        if ( instance == null )
            instance = new GAuthentication(credentials);
        return instance;
    }

    public void authenticateWithServiceAccount() throws  IOException, GeneralSecurityException {
        NetHttpTransport HTTPTransport = GoogleNetHttpTransport.newTrustedTransport();
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

    public GAuthentication setSpreadsheetID(String spreadsheetID) {
        this.spreadsheetID = spreadsheetID;
        return this;
    }

    public String getSpreadsheetID() {
        return spreadsheetID;
    }
}
