package com.github.megbailey.gsheets.mvc;

import com.github.megbailey.gsheets.GSheet;
import com.github.megbailey.gsheets.GSpreadsheet;

import com.google.api.services.sheets.v4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import java.util.logging.Logger;

@Service
public class GSpreadsheetService {
    private final GSpreadsheetManager gSpreadsheetManager;

    @Autowired
    public GSpreadsheetService(GSpreadsheetManager gSpreadsheetManager) {
        this.gSpreadsheetManager = gSpreadsheetManager;
    }

    //this.setColumns(this.spreadsheet.getRegularService().getData(this.getName(), "$A1:$Z1").get(0));



}
