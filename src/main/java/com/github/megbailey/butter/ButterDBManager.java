package com.github.megbailey.butter;

import com.github.megbailey.butter.domain.SampleObjectModel;

import java.io.IOException;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;

/*
 * This is the main thread that starts and holds the connection
  * with the Google API for queries on the spreadhseet.
*/
public class ButterDBManager {

    public static void main(String[] args)
            throws IOException, GAccessException, BadRequestException, IllegalAccessException, ResourceNotFoundException
    {
        ApplicationProperties properties = new ApplicationProperties("application.properties");

        /*  Get the client secret json file path from application properties */
        GAuthentication gAuthentication = GAuthentication.getInstance(properties.getCredentials());
        /*  Get the spreadsheet id from application properties */
        gAuthentication.setSpreadsheetID(properties.getSpreadsheetID())
                .authenticateWithServiceAccount();
        GSpreadsheet spreadsheet = new GSpreadsheet(gAuthentication);

        SampleObjectModel sample = new SampleObjectModel(spreadsheet);

        sample = sample.setCode("hello");
        sample.save();
    }

}
