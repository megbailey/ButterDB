package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.GException;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;

import java.util.*;

public class GSheet {
    private static final Map<Integer, String> IDDictionary = new HashMap<Integer, String>() {{
        put(1, "A");  put(2, "B");  put(3, "C");  put(4, "D");
        put(5, "E");  put(6, "F");  put(7, "G");  put(8, "H");
        put(9, "I");  put(10, "J"); put(11, "K"); put(12, "L");
        put(13, "M"); put(14, "N"); put(15, "O"); put(16, "P");
        put(17, "Q"); put(18, "R"); put(19, "S"); put(20, "T");
        put(21, "U"); put(22, "V"); put(23, "W"); put(24, "X");
        put(25, "Y"); put(26, "Z"); }};
    private HashBiMap<String, String> columnMap; // Label <-> ID
    private Integer ID;
    private String name;

    /* TODO:
    * - Insert data into spreadsheet: spreadsheets.values.append https://developers.google.com/sheets/api/reference/rest/v4/spreadsheets.values/append
    * - Delete data from the spreadsheet:
    */

    public GSheet() { }

    public GSheet setName(String newName) {
        this.name = newName;
        return this;
    }

    public GSheet setID(Integer newId) {
        this.ID = newId;
        return this;
    }

    public GSheet setColumns(List<Object> columns) {
        //If columns havent been named, skip
        if (columns == null) {
            return this;
        }

        this.columnMap = HashBiMap.create(columns.size());

        int columnCounter = 0;
        String label; String firstID; String secondID;
        Iterator<Object> iterator = columns.iterator();

        while ( iterator.hasNext() ) {
            columnCounter += 1;
            label = iterator.next().toString();

            // One char columnID
            if ( columnCounter < IDDictionary.size() ) {
                firstID = IDDictionary.get( columnCounter );
                this.columnMap.put( label, firstID );
            // Two char columnID
            } else if ( columnCounter > IDDictionary.size() ) {
                firstID = IDDictionary.get( columnCounter / IDDictionary.size() );
                secondID = IDDictionary.get( columnCounter % IDDictionary.size() );
                this.columnMap.put( label, firstID + secondID );
            }
        }
        return this;
    }

    /*
        Get the name of the sheet
    */
    public String getName() {
        return this.name;
    }


    /*
        Get the ID of the sheet
    */
    public Integer getID() {  return this.ID; }

    public HashBiMap<String, String> getColumnMap() {
        return this.columnMap;
    }


    /*
        Get all column labels.
    */
    public Set<String> getColumnLabels() {
        return this.columnMap.keySet();
    }

    /*
        Get all column IDs.
    */
    public Set<String> getColumnIDs()  {
        return this.columnMap.inverse().keySet();
    }


    /*
        Get a column ID from a label
    */
    public String getColumnID(String columnLabel) throws GException {
        String columnID = this.columnMap.get(columnLabel);
        if (columnID != null) { return columnID; }
        throw new GException();
    }

    /*
    Get a column label from an ID
    */
    public String getColumnLabel(String columnID) throws GException {
        String columnLabel = this.columnMap.inverse().get(columnID);
        if (columnLabel != null) { return columnLabel; }
        throw new GException();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, GSheet.class);
    }

}









