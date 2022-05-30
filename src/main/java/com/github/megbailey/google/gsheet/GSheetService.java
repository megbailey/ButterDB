package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.gsheet.GSheetModel;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class GSheetService {
    private final GSheetModel gSheetModel;

    @Autowired
    public GSheetService (GSheetModel gSheetModel) {
        this.gSheetModel = gSheetModel;
    }

    public JsonArray all(String tableName) {
        return gSheetModel.all(tableName);
    }

    public List<Object> all(Integer tableID) {
        return gSheetModel.all(tableID);
    }

}
