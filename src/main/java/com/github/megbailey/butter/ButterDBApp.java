package com.github.megbailey.butter;

import com.github.megbailey.butter.google.GLogHandler;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

/*
    This is the main thread which starts the ORM API for the DB.
*/
@SpringBootApplication
public class ButterDBApp {
    private static GLogHandler logger = new GLogHandler();

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ButterDBApp.class, args);
    }

    /*
        The GSpreadsheet bean is given to both the ButterDB & ButterTable Repositories
    */
    @Bean
    public GSpreadsheet getGSpreadsheet() throws GeneralSecurityException, IOException {
        InputStream stream =  ClassLoader.getSystemResourceAsStream("application.properties");
        Properties p = new Properties();
        p.load(stream);

        InputStream credentialsStream = ClassLoader.getSystemResourceAsStream(p.getProperty("google.client_secret"));
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        GAuthentication gAuthentication = GAuthentication.getInstance(credentials);
        gAuthentication.setSpreadsheetID(p.getProperty("google.spreadsheet_id"));
        gAuthentication.authenticateWithServiceAccount();

        GSpreadsheet gSpreadsheet = new GSpreadsheet(gAuthentication);
        return gSpreadsheet;
    }
}
