package com.github.megbailey.butter.google.api;

import com.github.megbailey.butter.google.exception.GoggleAccessException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class GAuthentication {
    private static GAuthentication instance;
    private static final Logger logger = LogManager.getLogger(GAuthentication.class.getName());
    private static final String APPLICATION_NAME = "GenericButterDBApp";
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

    public void authenticateWithServiceAccount() throws GoggleAccessException {
        try {
            NetHttpTransport HTTPTransport = GoogleNetHttpTransport.newTrustedTransport();
            this.sheetsService = new Sheets.Builder(HTTPTransport, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        } catch ( IOException | GeneralSecurityException e) {
            e.printStackTrace();
            throw new GoggleAccessException();
        }
        logger.info("Successfully authenticated with the Google API using a service account.");
    }


    public String getAccessToken() throws GoggleAccessException {
        AccessToken beforeRefresh = this.googleCredentials.getAccessToken();
        try {
            this.googleCredentials.refreshIfExpired();
        } catch ( IOException e) {
            e.printStackTrace();
            throw new GoggleAccessException();
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
