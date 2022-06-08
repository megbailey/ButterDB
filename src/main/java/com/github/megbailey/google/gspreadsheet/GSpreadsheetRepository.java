package com.github.megbailey.google.gspreadsheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class GSpreadsheetRepository {

    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public GSpreadsheetRepository(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }

//
//
//    public JsonArray create(String tableName) throws IOException {
//
//    }
//
//    public JsonArray delete(String tableName) throws IOException {
//
//    }


}
