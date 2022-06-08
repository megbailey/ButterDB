package com.github.megbailey.google.gsheet;

import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

}
