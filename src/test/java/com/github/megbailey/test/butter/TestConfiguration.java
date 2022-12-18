package com.github.megbailey.test.butter;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

@Configuration
public class TestConfiguration {


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

