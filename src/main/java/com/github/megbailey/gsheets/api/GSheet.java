package com.github.megbailey.gsheets.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GSheet {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String columnRange = "$A1:$Y1";
    private final GSpreadsheet spreadsheet;
    private Integer Id;
    private String name;
    private Map<String, String> columnIDDictionary;

    /* TODO:
    * - map the label name to the column ID (letter) for graph viz queries
    * - figure out how keep track of the filled range
    * - append data to next available row in spreadsheet
    */

    public GSheet(GSpreadsheet spreadsheet, String name, Integer Id) throws IOException {
        this.spreadsheet = spreadsheet;
        this.name = name;
        this.Id = Id;
        this.columnIDDictionary = new HashMap<>();
        // Add columns to the dictionary of label -> ID
        this.setColumnDictionary(this.spreadsheet.getRegularService().getData(this.getName(), this.columnRange).get(0));


    }

    public String getName() {
        return this.name;
    }

    public Integer getID() {
        return this.Id;
    }

    private void setName(String newName) {
        this.name = newName;
    }

    private void setID(Integer newId) {
        this.Id = newId;
    }

    private void setColumnDictionary(List<Object> columns) {

        String label; char curIDFirst = 'A'; char curIDSecond = 'A';
        Iterator<Object> iterator = columns.iterator();

        while ( iterator.hasNext() ) {
            label = iterator.next().toString();

            if ( curIDFirst >= 'A' & curIDFirst <= 'Z' ) {
                this.columnIDDictionary.put(label, curIDFirst+"");
                if (curIDFirst != 'Z')
                    curIDFirst += 1;
            } else if ( curIDSecond >= 'A' & label.charAt(1) <= 'Z' ) {
                this.columnIDDictionary.put(label, curIDFirst+""+curIDSecond);
                curIDSecond += 1;
            } else {
                System.out.println("Malformed Labels: " + columns.toString());
            }
        }

    }

    public Map<String, String> getColumnIDDictionary() {
        return this.columnIDDictionary;
    }


}