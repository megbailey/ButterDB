package com.github.megbailey.google.gspreadsheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class GSpreadsheetService {
    private final GSpreadsheetRepository gSpreadsheetRepository;

    @Autowired
    public GSpreadsheetService(GSpreadsheetRepository gSpreadsheetRepository) {
        this.gSpreadsheetRepository = gSpreadsheetRepository;
    }

    public boolean create(String tableName) {
        try {
            return this.gSpreadsheetRepository.createGSheet(tableName);
        } catch (IOException e) {
            System.out.println("Cannot create GSheet");
            return false;
        }
    }

    //this.setColumns(this.spreadsheet.getRegularService().getData(this.getName(), "$A1:$Z1").get(0));



}
