package com.github.megbailey.butter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.auth.oauth2.GoogleCredentials;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.GAccessException;

/*
    This is the main thread which starts the API.
*/
@SpringBootApplication
public class ButterDBApp {

    public static void main(String[] args) {
        SpringApplication.run(ButterDBApp.class, args);
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
        GAuthentication gAuthentication = GAuthentication.getInstance(credentials);
        /*  Get the spreadsheet id from application properties */
        gAuthentication.setSpreadsheetID(p.getProperty("google.spreadsheet_id"))
                .authenticateWithServiceAccount();
        GSpreadsheet gSpreadsheet = new GSpreadsheet(gAuthentication);
        return gSpreadsheet;
    }
}
