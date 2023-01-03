package com.github.megbailey.butter;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.GAccessException;
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

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(ButterDBApp.class, args);
    }

    /*
        The GSpreadsheet bean is given to both the ButterDB & ButterTable Repositories
    */
    @Bean
    public GSpreadsheet getGSpreadsheet() throws GAccessException, BadRequestException, IOException {
        /*  Get application properties */
        InputStream stream =  ClassLoader.getSystemResourceAsStream("application.properties");
        Properties p = new Properties();
        p.load(stream);
        System.out.println("Properties found: " + p.toString());
        /*  Get the client secret json file path from application properties */
        InputStream credentialsStream = ClassLoader.getSystemResourceAsStream(p.getProperty("google.client_secret"));
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        System.out.println("Creds found: " + credentials.toString());

        GAuthentication gAuthentication = GAuthentication.getInstance(credentials);
        /*  Get the spreadsheet id from application properties */
        gAuthentication.setSpreadsheetID(p.getProperty("google.spreadsheet_id"))
                .authenticateWithServiceAccount();
        GSpreadsheet gSpreadsheet = new GSpreadsheet(gAuthentication);
        return gSpreadsheet;
    }
}
