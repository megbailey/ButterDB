package com.github.megbailey.butter.table;

import com.github.megbailey.google.exception.GException;
import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.GSpreadsheet;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class GSheetRepository {
    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public GSheetRepository( GSpreadsheet gSpreadsheet ) {
        this.gSpreadsheet = gSpreadsheet;
    }


    public JsonArray all(String tableName) throws IOException {
        return this.gSpreadsheet.executeQuery( tableName );
    }

    public JsonArray query(String tableName, String constraints) throws IOException, GException {
        JsonArray result = this.gSpreadsheet.executeQuery( tableName, constraints );
        return result;
    }

    public ObjectModel append(String tableName, ObjectModel object) throws IOException {
        return this.gSpreadsheet.insert(tableName, object);
    }

}
