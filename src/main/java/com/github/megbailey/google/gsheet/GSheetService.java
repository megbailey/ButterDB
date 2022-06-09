package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.GException;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Hashtable;

@Service
public class GSheetService {
    private final GSheetRepository gSheetRepository;

    @Autowired
    public GSheetService(GSheetRepository gSheetRepository) {
        this.gSheetRepository = gSheetRepository;
    }

    public String getTable(String tableName) throws IOException {
        return this.gSheetRepository.getTable(tableName);
    }

    public JsonArray all(String tableName) throws IOException {
        return this.gSheetRepository.all(tableName);
    }

    public JsonArray query(String tableName, String constraints) throws IOException, GException {
        return this.gSheetRepository.query(tableName, constraints);
    }
}
