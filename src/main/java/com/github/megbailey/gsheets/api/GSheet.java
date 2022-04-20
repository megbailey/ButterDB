package com.github.megbailey.gsheets.api;

import com.github.megbailey.gsheets.api.request.APIRequestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.List;

public class GSheet {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final APIRequestController REQUEST_CONTROLLER;
    private final Integer ID;
    private String name;
    //private String dataRange;

    public GSheet(APIRequestController requestController, String name, Integer ID ) {
        this.REQUEST_CONTROLLER = requestController;
        this.ID = ID;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getID() {
        return this.ID;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public List<List<Object>> getData(String range) {
        try {
            return this.REQUEST_CONTROLLER.getData(this.name, range);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}