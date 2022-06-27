package com.github.megbailey.butter.table;

import com.github.megbailey.google.exception.GException;
import com.github.megbailey.butter.ObjectModel;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ButterTableService {
    private final ButterTableRepository butterTableRepository;

    @Autowired
    public ButterTableService(ButterTableRepository butterTableRepository) {
        this.butterTableRepository = butterTableRepository;
    }


    public JsonArray all(String tableName) {
        try {
            return this.butterTableRepository.all(tableName);
        } catch (IOException e) {
            JsonArray ar = new JsonArray();
            ar.add("noelements");
            return ar;
        }
    }

    public JsonArray query(String tableName, String constraints) {
        try {
            return this.butterTableRepository.query(tableName, constraints);
        } catch (IOException | GException e) {
            System.out.println("issue with query");
            return null;
        }
    }

    public ObjectModel create(String tableName, ObjectModel object) {
        try {
            return this.butterTableRepository.append(tableName, object);
        } catch (IOException e) {
            System.out.println("unable to append");
            e.printStackTrace();
            return null;
        }
    }
}
