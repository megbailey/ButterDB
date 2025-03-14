package com.github.megbailey.butter.google;

import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.common.collect.HashBiMap;
import com.google.gson.*;

import java.util.*;

public class GSheet {
    public final HashBiMap<Integer, String> IDDictionary = HashBiMap.create();
    private HashBiMap<String, String> columnMap; // Label <-> ID
    private Integer ID;
    private String name;

    public GSheet( ) {
        String[] cellIDs = new String[]{
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"
        };

        for (int i = 0; i < cellIDs.length; i++) {
            this.IDDictionary.put(( i + 1 ), cellIDs[i]);
        }
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
                this.columnMap.put( label, "" + firstID );
            // Two char columnID
            } else if ( columnCounter > IDDictionary.size() ) {
                firstID = IDDictionary.get( columnCounter / IDDictionary.size() );
                secondID = IDDictionary.get( columnCounter % IDDictionary.size() );
                this.columnMap.put( label, firstID + "" + secondID );
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
    /*public Set<String> getColumnIDs()  {
        return this.columnMap.inverse().keySet();
    }*/


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
    /*public String getColumnLabel(String columnID) throws ResourceNotFoundException {
        String columnLabel = this.columnMap.inverse().get(columnID);
        if (columnLabel != null) { return columnLabel; }
        throw new ResourceNotFoundException();
    }*/

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, GSheet.class);
    }

}









