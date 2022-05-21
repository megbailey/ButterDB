package com.github.megbailey.gsheets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final GSpreadsheetManager spreadsheet;
    private Map<String, String> columns;
    private Integer Id;
    private String name;

    /* TODO:
    * - figure out how keep track of the filled range
    * - append data to next available row in spreadsheet
    */

    public GSheet(GSpreadsheetManager spreadsheet, String name, Integer Id) throws IOException {
        this.spreadsheet = spreadsheet;
        this.name = name;
        this.Id = Id;
        this.columns = new HashMap<>();
        this.setColumns(this.spreadsheet.getRegularService().getData(this.getName(), "$A1:$Z1").get(0));
    }

    public String getName() { return this.name; }

    public Integer getID() {  return this.Id; }

    public Map<String, String> getColumns() { return this.columns; }

    public List<String> getColumns(List<String> labels) {
        List<String> IDs = new ArrayList<>();
        for ( String label: labels )  {
            IDs.add( this.getColumnID(label) );
        }
        return IDs;
    }

    private String getColumnID(String columnName) {
        String columnID = this.columns.get(columnName);
        if (columnID != null) { return columnID; }
        System.out.println("Column not found: " + columnName);
        return null;
    }

    private void setName(String newName) { this.name = newName; }

    private void setID(Integer newId) { this.Id = newId; }

    private void setColumns(List<Object> columns) {
        int columnCounter = 0;
        String label; String firstID; String secondID;
        Iterator<Object> iterator = columns.iterator();

        while ( iterator.hasNext() ) {

            label = iterator.next().toString();
            columnCounter += 1;

            if ( columnCounter < IDDictionary.size() ) { // One char columnID
                this.columns.put( label, IDDictionary.get( columnCounter ) );
            } else if ( columnCounter > IDDictionary.size() ) { // Two char columnID
                firstID = IDDictionary.get( columnCounter / IDDictionary.size() );
                secondID = IDDictionary.get( columnCounter % IDDictionary.size() );
                this.columns.put( label, firstID + secondID );
            }
        }
    }


}