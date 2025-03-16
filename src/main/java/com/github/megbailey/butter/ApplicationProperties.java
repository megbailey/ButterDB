package com.github.megbailey.butter;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {
    private final String spreadsheetID;
    private final GoogleCredentials credentials;

    public ApplicationProperties(String propertiesFilename) throws IOException {
        InputStream stream =  ClassLoader.getSystemResourceAsStream(propertiesFilename);
        Properties p = new Properties();
        p.load(stream);
        System.out.println(p);
        this.spreadsheetID = p.getProperty("google.spreadsheet_id");
        InputStream credentialsStream = ClassLoader.getSystemResourceAsStream(p.getProperty("google.client_secret"));
        assert credentialsStream != null;
        this.credentials = GoogleCredentials.fromStream(credentialsStream);
    }

    public String getSpreadsheetID() {
        return this.spreadsheetID;
    }

    public GoogleCredentials getCredentials() {
        return this.credentials;
    }
}