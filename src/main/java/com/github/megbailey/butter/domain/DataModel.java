package com.github.megbailey.butter.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.megbailey.butter.google.GSheet;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.BadResponse;
import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.google.api.client.util.ArrayMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class DataModel implements Serializable {
    private GSpreadsheet spreadsheet;
    private GSheet sheet;
    private String primaryKeyFieldName;
    private int autoIncrementCounterPrimaryKey = 1;
    private int rowCounter = 1; // start at row 1 to account for column labels
    private boolean wasRecentlyCreated;
    private Map<String, Field> modelFields = new HashMap<>();
    private List<String> currQueryConditions;
    private static Map<String,String> primaryKeyRowIndex;

    public DataModel() {
        this.currQueryConditions = new ArrayList<>();
    }

    protected DataModel(GSpreadsheet spreadsheet) {
        primaryKeyRowIndex = new ArrayMap<>();
        this.currQueryConditions = new ArrayList<>();
        this.spreadsheet = spreadsheet;

       try {
           // keep local store of fields defined in the model's class using reflection
           List<Object> fieldNameList = initReflectionFields();

            // Create a new sheet for this model if it does not exist and update column names to be synced with model
            this.sheet = this.spreadsheet.firstOrNewSheet(
                this.getClass().getSimpleName(),
                fieldNameList
            );

           // if primary key was given in model
           if ( this.primaryKeyFieldName != null ) {
               initCounters();
           }

       } catch (IOException | BadRequestException | IllegalAccessException | ResourceNotFoundException e) {
           e.printStackTrace();
           System.exit(-1);
       }
    }

    private List<Object> initReflectionFields() throws IllegalAccessException {
        Field[] fieldsInModel = this.getClass().getDeclaredFields();
        List<Object> fieldNameList = new ArrayList<>(fieldsInModel.length);


        for (Field fieldInModel : fieldsInModel) {
            fieldInModel.setAccessible(true);

            String fieldName = fieldInModel.getName();
            // determine which field should be used as the primary key
            if ( fieldName.equals("primaryKey") ) {
                this.primaryKeyFieldName = (String) fieldInModel.get(this);
            } else {
                this.modelFields.put(fieldName, fieldInModel);
                fieldNameList.add(fieldName);
            }
        }
        return fieldNameList;
    }

    private void initCounters() throws ResourceNotFoundException, BadRequestException {
        String columnLabel = this.sheet.getColumnID( this.primaryKeyFieldName );
        // grab a list of primary keys from the primary key column
        String primaryKeyRange = columnLabel + "2:" + columnLabel;
        List<List<Object>> listOfPrimaryKeys = this.spreadsheet.getWithRange(
                this.sheet.getName(),
                primaryKeyRange
        );


        // the sheet contains some primary keys
        if ( listOfPrimaryKeys != null ) {
            // set row counter based off # of primary keys in sheet. +1 for the next row
            this.rowCounter = listOfPrimaryKeys.size() + 1;

            for (List<Object> primaryKeyCell : listOfPrimaryKeys) {
                if ( !primaryKeyCell.isEmpty() ) {
                    int primaryKey = Integer.parseInt(primaryKeyCell.get(0).toString());
                    if (primaryKey >= this.autoIncrementCounterPrimaryKey) {
                        // set primary key counter based off # found in sheet. +1 for the next row
                        this.autoIncrementCounterPrimaryKey = primaryKey + 1;
                    }
                }
            }

        }
    }

    public void save() throws IllegalAccessException, BadRequestException, ResourceNotFoundException {
        Object[] rowDataInOrder = new Object[this.modelFields.size()];

        for (String fieldName : this.modelFields.keySet()) {

            Field fieldInModel = this.modelFields.get(fieldName);
            String columnIDForField = this.sheet.getColumnID(fieldName) + "";
            Integer fieldIndex = this.sheet.IDDictionary.inverse().get(columnIDForField);

            // this field represents the int primary key and it currently null
            if ( fieldIndex != null && Objects.equals(fieldName, this.primaryKeyFieldName) ) {
                Object primaryKey = fieldInModel.get(this);
                // set the primary key to the internal counter
                if ( primaryKey == null ) {
                    fieldInModel.set(this, this.autoIncrementCounterPrimaryKey);
                    this.autoIncrementCounterPrimaryKey += 1;
                }
                this.wasRecentlyCreated = true;
            }

            rowDataInOrder[fieldIndex-1] = fieldInModel.get(this);
        }

        if ( this.wasRecentlyCreated ) {
            // append the row to the sheet
            this.rowCounter += 1;
            String rangeForInsert = "A" + this.rowCounter + ":" + sheet.IDDictionary.get(this.modelFields.size()) + this.rowCounter;
            this.spreadsheet.insertRow(this.sheet.getName(), rangeForInsert, Arrays.stream(rowDataInOrder).toList());

        } /*else {
            // find where the row exists and update that row
            //this.spreadsheet.updateRow(sheetName, Arrays.stream(dataObjectsInOrder).toList());
        }*/
    }

    public List<DataModel> get() throws BadResponse, GAccessException, IOException, ResourceNotFoundException {
        ObjectMapper mapper = new ObjectMapper();

        // Data Model was not initialized with a spreadsheet
        if ( this.spreadsheet == null ) {
            // Produce error.
            throw new GAccessException("Unable to fetch from spreadsheet");
        }

        JsonArray jsonArr;
        if ( currQueryConditions.size() > 0 ) {
            // use query during get
            jsonArr = this.spreadsheet.findRows( this.sheet.getName(),  this.currQueryConditions.toArray(new String[0] ));
        } else {
            // otherwise, get all
            jsonArr = this.spreadsheet.getAllRows( this.sheet.getName() );
        }
        List<DataModel> objectList = new ArrayList<>(jsonArr.size());
        for (int i = 0; i < jsonArr.size(); i++) {
            JsonElement el = jsonArr.get(i);
            objectList.add( mapper.readValue(el.toString(), DataModel.class) );
        }


        return objectList;
    }

    public int lastInsertedID() {
        return autoIncrementCounterPrimaryKey - 1;
    }

    /*private String buildGVizQuery(GSheet gsheet, String[] constraints) {
        String[] transformedConstraints = new String[constraints.length];
        for (int i = 0; i < constraints.length; i++) {
            if (gsheet.getColumnMap().containsKey(constraints[i])) {
                transformedConstraints[i] = gsheet.getColumnMap().get(constraints[i]);
            } else {
                transformedConstraints[i] = constraints[i];
            }
        }

        String query = "select " + String.join(",", gsheet.getColumnIDs());
        if ( constraints.length > 0 ) {
            query += " where " + String.join("", transformedConstraints);
        }
        query = query.replaceAll(",", "%2C");;
        return query;
    }*/

    public void where(String column, String operator, String value) throws BadResponse, GAccessException, IOException, ResourceNotFoundException {
       /* this.spreadsheet.get( this.sheet.getName(), new String[]{
                column,
                operator,
                value
        });*/

    }

    /*
    public void Orwhere(String column, String operator, String value) {
        this.queryArr.add(column + operator + value);
    }*/

    public void delete() throws IllegalAccessException, BadResponse, GAccessException, IOException, ResourceNotFoundException {
        if ( this.primaryKeyFieldName == null || this.primaryKeyFieldName.isEmpty() || !this.modelFields.containsKey(this.primaryKeyFieldName) ) {
            System.out.println("Primary key\" " + primaryKeyFieldName + "\"  not found");
            System.exit(-1);
        }

        String primaryKeyValue = this.modelFields.get( this.primaryKeyFieldName ).get(this).toString();
        this.spreadsheet.deleteRow(this.sheet.getName(), new String []{
               this.primaryKeyFieldName, /* field */
               "=", /* operator */
               primaryKeyValue /* value */
       });
        this.rowCounter -= 1;
    }
}
