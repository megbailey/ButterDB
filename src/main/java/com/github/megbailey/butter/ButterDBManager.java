package com.github.megbailey.butter;

import java.io.IOException;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.GoggleAccessException;

/*
 * This class starts and holds the connection objects with Google for queries on the spreadsheet.
 * It's used by the models and must be called in the main thread of your application before any model is used.
*/
public class ButterDBManager {
    private static GAuthentication credentials;
    private static GSpreadsheet database;
    private static ApplicationProperties appConfiguration;
    private static String propertiesFilename = "application.properties";

    public ButterDBManager() throws IOException, GoggleAccessException {
        // only if connection isn't already established
        if ( database == null ) {
            this.configure();
            this.authenticate();
            this.connect();
        }
    }

    public ButterDBManager(String configPath) throws IOException, GoggleAccessException {
        // overrides default properties filename
        propertiesFilename = configPath;
        new ButterDBManager();
    }


    private void configure() throws IOException {
        appConfiguration = new ApplicationProperties(propertiesFilename);
    }

    private void connect() throws IOException {
        database = new GSpreadsheet(credentials);
    }

    private void authenticate() throws GoggleAccessException {
        credentials = GAuthentication.getInstance(appConfiguration.getCredentials());
        credentials.setSpreadsheetID(appConfiguration.getSpreadsheetID()).authenticateWithServiceAccount();
    }

    public static GSpreadsheet getDatabase() {
        return database;
    }

}
