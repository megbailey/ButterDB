package com.github.megbailey.test.butter;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Configuration
public class TestConfiguration {

    @Bean(name = "gSpreadsheet")
    public GSpreadsheet getGSpreadsheet() throws GeneralSecurityException, IOException {
        GAuthentication gAuthentication = GAuthentication.getInstance("1hKQc8R7wedlzx60EfS820ZH5mFo0gwZbHaDq25ROT34");
        gAuthentication.authenticateWithServiceAccount();
        GSpreadsheet gSpreadsheet = new GSpreadsheet(gAuthentication);
        return gSpreadsheet;
    }

}
