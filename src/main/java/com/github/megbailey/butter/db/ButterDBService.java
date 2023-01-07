package com.github.megbailey.butter.db;

import com.github.megbailey.butter.google.GSheet;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class ButterDBService {

    // Using an in-memory Map to store the object data
    // and makes API calls to Google on our behalf
    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public ButterDBService(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }

    /*
        Create a sheet
        @thrown SheetCreationException -> sheet by that name already exists
    */
    public GSheet create(String tableName) throws BadRequestException {
        try {
            return this.gSpreadsheet.addGSheet(tableName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadRequestException();
        }
    }

    /*
        Delete a sheet
        @thrown SheetNotFoundException -> sheet DNE
    */
    public GSheet delete(String tableName) throws ResourceNotFoundException {
        try {
            return this.gSpreadsheet.deleteGSheet(tableName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
    }

}
