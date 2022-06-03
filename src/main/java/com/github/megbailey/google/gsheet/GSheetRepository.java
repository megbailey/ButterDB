package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.api.request.APIVisualizationQueryUtility;
import com.github.megbailey.google.gspreadsheet.GSpreadsheet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class GSheetRepository {
    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public GSheetRepository(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }

    public String getTable(String tableName) throws IOException {
        return this.gSpreadsheet.getGSheet(tableName).toString();
    }

    public JsonArray all(String tableName) throws IOException {
        return this.gSpreadsheet.executeSelect("select *", tableName);
    }
}
