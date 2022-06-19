package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.GException;
import com.github.megbailey.google.ObjectModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public ObjectModel create(String tableName, ObjectModel object) {
        try {
            return this.gSheetRepository.append(tableName, object);
        } catch (IOException e) {
            System.out.println("unable to append");
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            System.out.println("object model not found");
            e.printStackTrace();
            return null;
        }
    }
}
