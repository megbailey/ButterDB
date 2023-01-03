package com.github.megbailey.butter.db;

import com.github.megbailey.butter.google.GSheet;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;


@Repository
public class ButterDBRepository {

    // Using an in-memory Map to store the object data
    // and makes API calls to Google on our behalf
    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public ButterDBRepository(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }

    public GSheet createGSheet(String tableName) throws IOException, BadRequestException {
        return this.gSpreadsheet.addGSheet(tableName);
    }


    public GSheet deleteGSheet(String tableName) throws IOException, ResourceNotFoundException {
        return this.gSpreadsheet.deleteGSheet(tableName);
    }


}
