package com.github.megbailey.butter;

import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.SystemErrorException;
import com.github.megbailey.butter.google.exception.GoggleAccessException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class Model {
    private GSpreadsheet spreadsheet;
    private final String modelName; 
    private final String primaryKey;
    private final List<String> fields;
    private final boolean autoIncrementPK;
    private int autoIncrementPKCounter = 1;
    private int occupiedRowCounter = 1; // start at row 1 to account for column labels
    private boolean wasRecentlyCreated;
    //private boolean exists;
    // Attribute Name <--> ColumnID
    private final HashBiMap<String, String> attributeIDMap = HashBiMap.create();
    // Attribute Name --> Attribute Value
    private final Map<String, Object> attributeValuesMap = new HashMap<>();
    private final List<String> currQueryConditions;


    //private static Map<String,String> primaryKeyRowIndex;

    protected Model( String pkField, String[] fields, boolean autoIncrementPK ) {
        primaryKey = pkField;
        this.fields = List.of(fields);

        currQueryConditions = new ArrayList<>();
        this.autoIncrementPK = autoIncrementPK;

        modelName = getClass().getSimpleName();
    }

    private void before() {
        try {
            // Initialize model with a spreadsheet instance if it doesn't have one
            if ( spreadsheet == null ) {
                spreadsheet = ButterDBManager.getDatabase();
            }

            // Create a new sheet for this model if it does not exist
            spreadsheet.firstOrNewSheet( modelName );

            if ( attributeIDMap.size() == 0 ) {
                // Init existing and any new model attributes
                initAttributes();
            }

            System.out.println("attributeIDMap --> " + attributeIDMap);
            // Optionally, count PK field for to prep for next entry
            if ( autoIncrementPK ) {
                initAutoIncrementPK();
            }

            // on next call set to false
            if ( wasRecentlyCreated ) {
                wasRecentlyCreated = false;
            }

        } catch (IOException | BadRequestException | ResourceNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


    public void save() throws BadRequestException, ResourceNotFoundException {
        // setup
        before();

        // The primary key is null, and user wants it to autoincrement;
        Object pk = attributeValuesMap.get( primaryKey );
        if ( pk == null && autoIncrementPK ) {
            pk = incrementPK();
            attributeValuesMap.put(primaryKey, pk);
            wasRecentlyCreated = true;
        }

        // Put model's data into an array of ambiguous objects for sending
        Object[] rowDataInOrder = new Object[attributeIDMap.size()];
        BiMap<String, String> cellIDToAttributeName = attributeIDMap.inverse();

        for ( int i = 0; i < cellIDToAttributeName.size(); i++) {
            String cellID = spreadsheet.cellIDsMap.get(i);
            String attributeName = cellIDToAttributeName.get(cellID);
            Object attributeValue = attributeValuesMap.get(attributeName);
            // Add data and record its cellID
            rowDataInOrder[i] = attributeValue;
        }


        if ( wasRecentlyCreated ) {
            String firstCellInRange = "A" + occupiedRowCounter;
            String lastCellInRange = spreadsheet.cellIDsMap.get( attributeIDMap.size() ) + occupiedRowCounter;

            // append the row to the end of the sheet
            occupiedRowCounter += 1;

            System.out.println("Insert " + modelName + "@ " + firstCellInRange + ":" + lastCellInRange);
            spreadsheet.insertRow(modelName, firstCellInRange + ":" + lastCellInRange, Arrays.stream(rowDataInOrder).toList());
            //exists = true;
        } else if ( pk != null ) {
            // find where the row exists and update that row
            //spreadsheet.updateRow(sheetName, Arrays.stream(dataObjectsInOrder).toList());

            Model found = where(primaryKey, "=", pk.toString());
            System.out.println(found);
        }
    }

    public Object getFieldValue(String fieldName) {
        return attributeValuesMap.get(fieldName);
    }

    public void setFieldValue(String fieldName, Object fieldValue) {
        if ( attributeValuesMap.containsKey(fieldName) ) {
            attributeValuesMap.replace(fieldName, fieldValue);
        } else {
            attributeValuesMap.put(fieldName, fieldValue);
        }
    }

    public Model get() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        JsonArray jsonArr;
        if ( currQueryConditions.size() > 0 ) {
            // use query during get
            String findQuery = buildGVizSelect( null ) +
                                " where " + String.join("", currQueryConditions);
            System.out.println("GVizSelect --> " + findQuery);
            jsonArr = spreadsheet.findRows( modelName, findQuery);
            // empty query conditions once used
            currQueryConditions.clear();
        } else {
            // otherwise, get all
            jsonArr = spreadsheet.getAll( modelName, attributeIDMap.values() );
        }

        // TODO: Update so that this returns collection of Models rather than 1
        BiMap<String,String> cellIDAttributeMap = this.attributeIDMap.inverse();
        for ( JsonElement el: jsonArr ) {
            JsonArray row = el.getAsJsonObject().get("c").getAsJsonArray();
            // for each json object in array
            // get the attribute name given the index;
            for (int i = 0; i < row.size(); i++) {
                JsonElement cellElement = row.get(i);
                JsonPrimitive cellValue = null;
                if ( !cellElement.isJsonNull() ) {
                    JsonObject cellObject = cellElement.getAsJsonObject();
                    //System.out.println(cellObject + " has f " + cellObject.has("f"));
                    // Prioritize a cell's "f" value over the "v" value if it exists.
                    // "f" value will contain a string version of a value
                    if ( cellObject.has("f") ) {
                        JsonElement fValue = cellObject.get("f");
                        if ( !fValue.isJsonNull() ) {
                            cellValue = fValue.getAsJsonPrimitive();
                        }
                    } else {
                        JsonElement vValue = cellObject.get("v");
                        if ( !vValue.isJsonNull() ) {
                            cellValue = vValue.getAsJsonPrimitive();
                        }
                    }
                }

                String cellID = this.spreadsheet.cellIDsMap.get(i);
                String attributeName = cellIDAttributeMap.get(cellID);
                Object castedValue = this.safeCastCellValue(cellValue);
                //System.out.println( cellID + " Putting value " + castedValue + " in " + attributeName);
                this.attributeValuesMap.put(attributeName, castedValue);

            }
        }
        return this;
    }


    public Model find(Integer primaryKeyValue) throws SystemErrorException, IOException, GoggleAccessException, ResourceNotFoundException {
        return where( primaryKey, "=", primaryKeyValue ).get();
    }
    
    public Model where(String column, String operator, Object value) {
        String condition = buildGVizWhereCondition(new Object[]{ column, operator, value });
        if ( !currQueryConditions.isEmpty() ) {
            currQueryConditions.add(" and ");
            currQueryConditions.add(condition);
        } else {
            currQueryConditions.add(condition);
        }
        return this;
    }

   /* public Model where(String column, Object value) {
        return this.where(column, "=", value);
    }*/

    public Model orWhere(String column, String operator, Object value) {
        String condition = buildGVizWhereCondition(new Object[]{ column, operator, value });
        if ( !currQueryConditions.isEmpty() ) {
            currQueryConditions.add(" or ");
            currQueryConditions.add(condition);
        } else {
            currQueryConditions.add(condition);
        }
        return this;
    }

    public Boolean delete() throws SystemErrorException, GoggleAccessException, IOException, ResourceNotFoundException {
        if ( primaryKey == null || primaryKey.isEmpty() || !attributeValuesMap.containsKey(primaryKey) ) {
            System.out.println("Primary key\" " + primaryKey + "\"  not found");
            System.exit(-1);
        }

        String primaryKeyValue = attributeValuesMap.get( primaryKey ).toString();
        Boolean result = spreadsheet.deleteRow(
            modelName,
            attributeIDMap.values(),
            "where " + attributeIDMap.get( primaryKey ) + "=" + primaryKeyValue
        );
        if ( result ) {
            occupiedRowCounter -= 1;
        }
        return result;
    }

    private void addAttributeToIDMap(String attrName, int i) {
        String firstID; String secondID;
        // One char column ID
        if ( i < spreadsheet.cellIDsMap.size() ) {
            firstID = spreadsheet.cellIDsMap.get( i );
            attributeIDMap.put( attrName, firstID );
        }
        // Two char column ID
        else {
            firstID = spreadsheet.cellIDsMap.get( i / spreadsheet.cellIDsMap.size() );
            secondID = spreadsheet.cellIDsMap.get( i % spreadsheet.cellIDsMap.size() );
            attributeIDMap.put( attrName, firstID + secondID );
        }
    }

    private void initAttributes() throws BadRequestException, ResourceNotFoundException {
        // If attributes haven't been provided, skip initializing
        if ( fields.size() == 0 ) {
            return;
        }

        // Get the first row of the spreadsheet which should have the attribute identifiers
        List<Object> firstRow = spreadsheet.getWithRange(modelName, "A1:ZZ1").get(0);

        // Associate any existing columns with a column ID first
        for (int i = 0; i < firstRow.size(); i++) {
            String columnName = firstRow.get(i).toString();
            addAttributeToIDMap(columnName, i);
        }

        // Determine if columns have been provisioned in the sheet for user-identified attributes
        for (int i = 0; i < fields.size(); i++) {
            String attributeName = fields.get(i);
            String attributeID = attributeIDMap.get(attributeName);
            // If columns have not been provisioned, add to map, and send a request to re-write first row
            if ( attributeID == null ) {
                addAttributeToIDMap(attributeName, i);
                spreadsheet.updateRow(modelName, Arrays.asList(attributeIDMap.keySet().toArray()));
            }
        }

    }

    private void initAutoIncrementPK() throws ResourceNotFoundException, BadRequestException {
        String columnID = attributeIDMap.get( primaryKey );
        if ( columnID == null ) {
            throw new RuntimeException("Primary Key field not identified");
        }

        // Select all primary keys from the sheet
        String primaryKeyRange = columnID + "2:" + columnID;
        List<List<Object>> allPrimaryKeys = spreadsheet.getWithRange( modelName, primaryKeyRange );

        // The sheet contains some primary keys
        if ( allPrimaryKeys != null ) {
            // set row counter based off # of primary keys in sheet. +1 for the next row
            occupiedRowCounter = allPrimaryKeys.size() + 1;

            for (List<Object> primaryKeyCell : allPrimaryKeys) {
                if ( !primaryKeyCell.isEmpty() ) {
                    int primaryKey = Integer.parseInt(primaryKeyCell.get(0).toString());
                    if (primaryKey >= autoIncrementPKCounter) {
                        // set primary key counter relative to found integers in the sheet.
                        incrementPK();
                    }
                }
            }

        }
    }

    private Integer incrementPK() {
        int currPK = autoIncrementPKCounter;
        autoIncrementPKCounter += 1;
        return currPK;
    }

    private String buildGVizSelect( String[] attrsToSelect ) {
        this.before();

        StringBuilder query = new StringBuilder("select ");
        // None specified, Include all columns
        if ( attrsToSelect == null || attrsToSelect.length == 0 ) {
            query.append( String.join(",", attributeIDMap.values() ));
        }
        // Include only listed fields
        else {
            for ( String attr : attrsToSelect ) {
                query.append( attributeIDMap.get(attr) ).append(",");
            }
        }
        return query.toString();
    }

    private String buildGVizWhereCondition(Object[] constraintSegments ) {
        this.before();

        String column = constraintSegments[0].toString();
        String operator = constraintSegments[1].toString();
        Object value = constraintSegments[2];

        if ( attributeIDMap.containsKey(column) ) {
            column = attributeIDMap.get(column);
        }

        if ( value.getClass() == String.class ) {
            value = "'" + value + "'";
        }

        return "(" + column + operator + value + ")";
    }

    private Object safeCastCellValue(JsonPrimitive primitiveCellValue) {
        if ( primitiveCellValue == null ) {
            return null;
        }

        // String is the default object type chosen if no other types could be safely cast
        String stringCellValue = primitiveCellValue.getAsString();

        try {
            return Integer.parseInt(stringCellValue);
        } catch ( NumberFormatException exception ) { /* do nothing; continue on */ }
        try {
            return Float.parseFloat(stringCellValue);
        } catch ( NumberFormatException exception ) { /* do nothing; continue on */ }
        try {
            return new BigInteger(stringCellValue);
        } catch ( NumberFormatException exception ) { /* do nothing; continue on */ }
        try {
            return new BigDecimal(stringCellValue);
        } catch ( NumberFormatException exception ) { /* do nothing; continue on */ }

        return stringCellValue;
    }
}
