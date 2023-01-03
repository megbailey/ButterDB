package com.github.megbailey.butter.google.api;

import com.github.megbailey.butter.google.exception.GAccessException;
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
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class GAuthentication {
    private static GAuthentication instance;
    private static Logger logger = Logger.getLogger(GAuthentication.class.getName());
    private static final String APPLICATION_NAME = "ButterDB app";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private final GoogleCredentials googleCredentials;
    private String spreadsheetID;
    private Sheets sheetsService;


    private GAuthentication(GoogleCredentials credentials) {
        this.googleCredentials = credentials.createScoped(SCOPES);
    }

    public static GAuthentication getInstance(GoogleCredentials credentials) {
        if ( instance == null )
            instance = new GAuthentication(credentials);
        return instance;
    }

    public Sheets getSheetsService() {
        return this.sheetsService;
    }

    public void authenticateWithServiceAccount() throws GAccessException {
        try {
            NetHttpTransport HTTPTransport = GoogleNetHttpTransport.newTrustedTransport();
            this.sheetsService = new Sheets.Builder(HTTPTransport, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch ( IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new GAccessException();
        }
        logger.info("Successfully authenticated with the Google API using a service account.");
    }


    public String getAccessToken() throws GAccessException {
        AccessToken beforeRefresh = this.googleCredentials.getAccessToken();
        try {
            this.googleCredentials.refreshIfExpired();
        } catch ( IOException e) {
            e.printStackTrace();
            throw new GAccessException();
        }
        if ( !beforeRefresh.toString().equals( this.googleCredentials.getAccessToken().toString() ))
            logger.info("Refreshed Google Access Token");
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
