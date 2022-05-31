package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.api.request.APIVisualizationQueryUtility;
import com.github.megbailey.google.gspreadsheet.GSpreadsheet;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class GSheetRepository {
    private final GSpreadsheet gSpreadsheet;
    private APIVisualizationQueryUtility gVizRequestUtility;

    @Autowired
    public GSheetRepository(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }

    public JsonArray all(String tableName) throws IOException {
        Integer sheetID = this.gSpreadsheet.getGSheetID(tableName);
        return this.gVizRequestUtility.executeGVizQuery("select *", sheetID);
    }
}
