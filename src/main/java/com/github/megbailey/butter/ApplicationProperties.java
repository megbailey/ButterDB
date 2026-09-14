package com.github.megbailey.butter;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {
    private final String spreadsheetID;
    private final GoogleCredentials credentials;

    public ApplicationProperties(String propertiesFilename) throws IOException {
        ClassLoader classLoader = resolveClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(propertiesFilename)) {
            if (stream == null) {
                throw new IOException("Missing classpath resource: " + propertiesFilename);
            }
            Properties p = new Properties();
            p.load(stream);
            this.spreadsheetID = p.getProperty("google.spreadsheet_id");
            String credentialsResource = p.getProperty("google.client_secret");
            try (InputStream credentialsStream = classLoader.getResourceAsStream(credentialsResource)) {
                if (credentialsStream == null) {
                    throw new IOException("Missing classpath resource: " + credentialsResource);
                }
                this.credentials = GoogleCredentials.fromStream(credentialsStream);
            }
        }
    }

    private static ClassLoader resolveClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ApplicationProperties.class.getClassLoader();
        }
        return classLoader;
    }

    public String getSpreadsheetID() {
        return this.spreadsheetID;
    }

    public GoogleCredentials getCredentials() {
        return this.credentials;
    }
}
