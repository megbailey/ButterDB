package com.github.megbailey.google.gsheet;

import com.github.megbailey.google.GException;
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
    private Map<String, String> columns;
    private Integer ID;
    private String name;

    /* TODO:
    * - figure out how keep track of the filled range
    * - append data to next available row in spreadsheet
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
        this.columns = new HashMap<>();

        //If columns havent been named, skip
        if (columns == null) {
            return this;
        }

        int columnCounter = 0;
        String label; String firstID; String secondID;
        Iterator<Object> iterator = columns.iterator();

        while ( iterator.hasNext() ) {
            columnCounter += 1;
            label = iterator.next().toString();

            // One char columnID
            if ( columnCounter < IDDictionary.size() ) {
                this.columns.put( label, IDDictionary.get( columnCounter ) );
            // Two char columnID
            } else if ( columnCounter > IDDictionary.size() ) {
                firstID = IDDictionary.get( columnCounter / IDDictionary.size() );
                secondID = IDDictionary.get( columnCounter % IDDictionary.size() );
                this.columns.put( label, firstID + secondID );
            }
        }
        return this;
    }

    public String getName() { return this.name; }

    public Integer getID() {  return this.ID; }

    public Map<String, String> getColumnMap() { return this.columns; }

    public Set<String> getColumns() { return this.columns.keySet(); }

    /*
        Get all column IDs.
    */
    public Collection<String> getColumnIDs() throws GException {
        return this.columns.values();
    }

    /*
       Get some column IDs from their labels
     */
    public Set<String> getColumnIDs(Set<String> labels) throws GException {
        Set<String> IDs = new HashSet<>();
        for ( String label: labels )  {
            IDs.add( this.getColumnID(label) );
        }
        return IDs;
    }

    /*
        Get a column ID from a label
    */
    public String getColumnID(String columnLabel) throws GException {
        String columnID = this.columns.get(columnLabel);
        if (columnID != null) { return columnID; }
        throw new GException();
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this, GSheet.class);
    }

}









