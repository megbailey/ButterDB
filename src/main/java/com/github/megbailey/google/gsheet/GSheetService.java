package com.github.megbailey.google.gspreadsheet;

import com.github.megbailey.google.gsheet.GSheetRepository;
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

    public JsonArray getGSheet(String tableName) throws IOException {
        return this.gSheetRepository.all(tableName);
    }


}
