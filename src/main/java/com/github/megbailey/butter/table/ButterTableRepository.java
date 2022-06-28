package com.github.megbailey.butter.table;

import com.github.megbailey.google.exception.*;
import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.GSpreadsheet;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ButterTableRepository {
    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public ButterTableRepository(GSpreadsheet gSpreadsheet ) {
        this.gSpreadsheet = gSpreadsheet;
    }

    public JsonArray all(String tableName)
            throws GAccessException, SheetNotFoundException, CouldNotParseException {
        return this.gSpreadsheet.executeQuery( tableName );
    }

    public JsonArray query(String tableName, String constraints)
            throws GAccessException, SheetNotFoundException, CouldNotParseException {
        JsonArray result = this.gSpreadsheet.executeQuery( tableName, constraints );
        return result;
    }

    public ObjectModel append(String tableName, ObjectModel object)
            throws InvalidInsertionException, SheetNotFoundException {
        return this.gSpreadsheet.insert(tableName, object);
    }

}
