package com.github.megbailey.butter.db;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadResponse;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.github.megbailey.butter.domain.ObjectModel;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class ButterTableService {
    // Using an in-memory Map to store the object data
    // and makes API calls to Google on our behalf
    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public ButterTableService(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }

    public JsonArray all(String tableName) throws GAccessException, ResourceNotFoundException, BadResponse, IOException {
        return this.gSpreadsheet.get( tableName );
    }

    public JsonArray query(String tableName, String query)
            throws GAccessException, BadRequestException, ResourceNotFoundException, BadResponse, IOException {
        return this.gSpreadsheet.find( tableName, query );
    }

    public List<ObjectModel> create(String tableName, List<ObjectModel> objects)
            throws BadRequestException, ResourceNotFoundException {
        return this.gSpreadsheet.insert(tableName, objects);
    }

    public boolean delete(String tableName, String query)
            throws ResourceNotFoundException, GAccessException, IOException, BadResponse {
        return this.gSpreadsheet.delete(tableName, query);
    }

}
