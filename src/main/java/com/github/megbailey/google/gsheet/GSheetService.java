package com.github.megbailey.google.gsheet;

import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GSheetService {
    private final GSheetRepository gSheetRepository;

    @Autowired
    public GSheetService (GSheetRepository gSheetRepository) {
        this.gSheetRepository = gSheetRepository;
    }

//    public JsonArray all(String tableName) {
//        return this.gSheetRepository.all(tableName);
//    }
//
//    public List<Object> all(Integer tableID) {
//        return this.gSheetRepository.all(tableID);
//    }

}
