package com.github.megbailey.butter.table;

import com.github.megbailey.google.exception.*;
import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.GSpreadsheet;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ButterTableRepository {
    // Using an in-memory Map to store the object data
    // and makes API calls to Google on our behalf
    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public ButterTableRepository(GSpreadsheet gSpreadsheet ) {
        this.gSpreadsheet = gSpreadsheet;
    }

    public JsonArray all(String tableName)
            throws GAccessException, ResourceNotFoundException {
        return this.gSpreadsheet.executeQuery( tableName );
    }

    public JsonArray query(String tableName, String constraints)
            throws GAccessException, ResourceNotFoundException, NullPointerException {
        JsonArray result = this.gSpreadsheet.executeQuery( tableName, constraints );
        return result;
    }

    public List<ObjectModel> append(String tableName, List<ObjectModel> objects)
            throws InvalidInsertionException, ResourceNotFoundException {
        return this.gSpreadsheet.insert(tableName, objects);
    }

}
