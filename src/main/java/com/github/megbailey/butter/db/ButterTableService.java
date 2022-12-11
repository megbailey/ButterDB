package com.github.megbailey.butter.db;

import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.InvalidInsertionException;
import com.github.megbailey.butter.google.exception.InvalidQueryException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.github.megbailey.butter.domain.ObjectModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;


@Service
public class ButterTableService {
    private final ButterTableRepository butterTableRepository;

    @Autowired
    public ButterTableService(ButterTableRepository butterTableRepository) {
        this.butterTableRepository = butterTableRepository;
    }

    public JsonArray all(String tableName)
            throws GAccessException, ResourceNotFoundException, ClassNotFoundException {
        JsonArray results = this.butterTableRepository.all(tableName);
        return addClassProperty(tableName, results);
    }

    public JsonArray query(String tableName, String constraints)
            throws GAccessException, ResourceNotFoundException, InvalidQueryException, ClassNotFoundException {
        JsonArray results = this.butterTableRepository.query(tableName, constraints);
        return addClassProperty(tableName, results);
    }

    public List<ObjectModel> create(String tableName, List<ObjectModel> objects)
            throws InvalidInsertionException, ResourceNotFoundException {
        return this.butterTableRepository.append(tableName, objects);
    }

    private JsonArray addClassProperty(String tableName, JsonArray data) throws ClassNotFoundException {
        String thisPackage = this.getClass().getPackageName();
        // Interface implementations are placed in this package
        String domainPackage = thisPackage.substring(0, thisPackage.lastIndexOf('.') +1 ) + "domain";
        System.out.println(domainPackage);
        //Class exists
        Class om = Class.forName(domainPackage + "." + tableName);
        //Add @class property from returned values
        for (JsonElement el: data ) {
            JsonObject obj = el.getAsJsonObject();
            JsonElement classEl = new JsonPrimitive(om.getCanonicalName());
            obj.add("@class", classEl);
        }
        return data;
    }
}
