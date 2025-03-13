package com.github.megbailey.butter.google;

import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;

import java.util.*;

public class GSheet {
    public final HashBiMap<Integer, String> IDDictionary = HashBiMap.create();
    private HashBiMap<String, String> columnMap; // Label <-> ID
    private Integer ID;
    private String name;public GSheet( ) {
        this.IDDictionary.put(1, "A"); this.IDDictionary.put(2, "B"); this.IDDictionary.put(3, "C");
        this.IDDictionary.put(4, "D"); this.IDDictionary.put(5, "E"); this.IDDictionary.put(6, "F");
        this.IDDictionary.put(7, "G"); this.IDDictionary.put(8, "H"); this.IDDictionary.put(9, "I");
        this.IDDictionary.put(10, "J"); this.IDDictionary.put(11, "K"); this.IDDictionary.put(12, "L");
        this.IDDictionary.put(13, "M"); this.IDDictionary.put(14, "N"); this.IDDictionary.put(15, "O");
        this.IDDictionary.put(16, "P"); this.IDDictionary.put(17, "Q"); this.IDDictionary.put(18, "R");
        this.IDDictionary.put(19, "S"); this.IDDictionary.put(20, "T"); this.IDDictionary.put(21, "U");
        this.IDDictionary.put(22, "V"); this.IDDictionary.put(23, "W"); this.IDDictionary.put(24, "X");
        this.IDDictionary.put(25, "Y"); this.IDDictionary.put(26, "Z");
    }

    public GSheet setName(String newName) {
        this.name = newName;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public GSheet setID(Integer newId) {
        this.ID = newId;
        return this;
    }

    public Integer getID() {
        return this.ID;
    }

    /*
        Store the known columns of a google sheet. Creates a bidirectional map of the label and ID.
     */
    public GSheet setColumns(List<Object> columns) {
        System.out.println("in set columns " + columns);
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
        Get the mapping of column label <-> ID
     */
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
    public String getColumnID(String columnLabel) throws ResourceNotFoundException {
        String columnID = this.columnMap.get(columnLabel);
        if (columnID != null) { return columnID; }
        throw new ResourceNotFoundException();
    }

    /*
        Get a column label from an ID
    */
    public String getColumnLabel(String columnID) throws ResourceNotFoundException {
        String columnLabel = this.columnMap.inverse().get(columnID);
        if (columnLabel != null) { return columnLabel; }
        throw new ResourceNotFoundException();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, GSheet.class);
    }

}









