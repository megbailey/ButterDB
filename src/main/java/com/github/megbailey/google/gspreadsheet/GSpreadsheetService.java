package com.github.megbailey.google.gspreadsheet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GSpreadsheetService {
    private final GSpreadsheetModel gSpreadsheetModel;

    @Autowired
    public GSpreadsheetService(GSpreadsheetModel gSpreadsheetModel) {
        this.gSpreadsheetModel = gSpreadsheetModel;
    }

    //this.setColumns(this.spreadsheet.getRegularService().getData(this.getName(), "$A1:$Z1").get(0));



}
