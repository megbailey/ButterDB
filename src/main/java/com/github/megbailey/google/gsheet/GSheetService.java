package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.GException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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

    public JsonArray all(String tableName) {
        try {
            return this.gSheetRepository.all(tableName);
        } catch (IOException e) {
            JsonArray ar = new JsonArray();
            ar.add("noelements");
            return ar;
        }
    }

    public JsonArray query(String tableName, String constraints) {
        try {
            return this.gSheetRepository.query(tableName, constraints);
        } catch (IOException | GException e) {
            System.out.println("issue with query");
            return null;
        }
    }

//    public JsonObject create(String tableName, Object object) {
//        try {
//            return this.gSheetRepository.create(tableName, object);
//        } catch (IOException e) {
//            System.out.println("unable to append");
//            return null;
//        }
//    }
}
