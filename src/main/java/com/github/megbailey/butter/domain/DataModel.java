package com.github.megbailey.butter.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.megbailey.butter.google.GSheet;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class DataModel implements Serializable {
    private GSpreadsheet spreadsheet;
    private GSheet sheet;
    private String primaryKeyFieldName;
    private int autoIncrementCounterPrimaryKey;
    private int rowCounter;
    private boolean wasRecentlyCreated;
    private Map<String, Field> modelFields = new HashMap<>();
    private ArrayList<String> queryArr;

    public DataModel(GSpreadsheet spreadsheet) {
        this.queryArr = new ArrayList<>();
        this.spreadsheet = spreadsheet;
        this.autoIncrementCounterPrimaryKey = 1;

       try {
           // get the field names currently defined in the model's class
           Field[] dataModelFields = this.getDeclaredFieldList(this.getClass());
           List<Object> fieldNameList = new ArrayList<>(dataModelFields.length);

           for (Field fieldForModel: dataModelFields) {
               String fieldName = fieldForModel.getName();
               // determine which field should be used as the primary key
               if ( fieldName.equals("primaryKey") ) {
                   this.primaryKeyFieldName = (String) fieldForModel.get(this);
               } else {
                   this.modelFields.put(fieldName, fieldForModel);
                   fieldNameList.add(fieldName);
               }
           }

            // Create a new sheet for this model if it does not exist and update column names to be synced with model
            this.sheet = this.spreadsheet.firstOrNewSheet(
                this.getClass().getSimpleName(),
                fieldNameList
            );

           // if primary key was given in model
           if ( this.primaryKeyFieldName != null ) {

               String columnLabel = this.sheet.getColumnID( this.primaryKeyFieldName );
               // grab a list of the primary keys that have already been used
               List<List<Object>> listOfPrimaryKeys = this.spreadsheet.getWithRange(
                   this.sheet.getName(),
                   columnLabel + "2:" + columnLabel
               );

               if ( listOfPrimaryKeys != null ) {
                   // set row counter based off # of primary keys in sheet
                   this.rowCounter = listOfPrimaryKeys.size() + 2;
                   // set primary key counter based off ones found in sheet
                   for (List<Object> primaryKeyCell : listOfPrimaryKeys) {
                       int primaryKey = Integer.parseInt( primaryKeyCell.get(0).toString() ) ;
                       if ( primaryKey >= this.autoIncrementCounterPrimaryKey ) {
                           this.autoIncrementCounterPrimaryKey = primaryKey + 1;
                       }

                   }
               }

           }
           System.out.println("row count " + rowCounter + " primary key " + this.autoIncrementCounterPrimaryKey);

       } catch (IOException | BadRequestException | IllegalAccessException | ResourceNotFoundException e) {
           e.printStackTrace();
           System.exit(-1);
       }
    }

    private Field[] getDeclaredFieldList(Class clazz) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //System.out.println( field.getName() + " " + field.get(this));
        }
        return fields;
    }

    public void save() throws IllegalAccessException, BadRequestException, ResourceNotFoundException {
        Object[] dataObjectsInOrder = new Object[this.modelFields.size()];
        for (String fieldName : this.modelFields.keySet()) {
            Field fieldInModel = this.modelFields.get(fieldName);
            String columnIDForField = this.sheet.getColumnID(fieldName);
            Integer fieldIndex = this.sheet.IDDictionary.inverse().get(columnIDForField)-1;

            // this field represents the int primary key and it currently null
            if ( Objects.equals(fieldName, this.primaryKeyFieldName) ) {
                Object primaryKey = fieldInModel.get(this);
                // set the primary key to the internal counter
                if ( primaryKey == null ) {
                    fieldInModel.set(this, this.autoIncrementCounterPrimaryKey);
                    this.autoIncrementCounterPrimaryKey = this.autoIncrementCounterPrimaryKey + 1;
                }
                this.wasRecentlyCreated = true;
            }

            dataObjectsInOrder[fieldIndex] = fieldInModel.get(this);
        }

        String sheetName = this.sheet.getName();
        if ( this.wasRecentlyCreated ) {
            // append the row to the sheet
            this.spreadsheet.insert(sheetName, Arrays.stream(dataObjectsInOrder).toList(), this.rowCounter);
            this.rowCounter += 1;
        } else {
            // find where the row exists and update that row
            this.spreadsheet.updateRow(sheetName, Arrays.stream(dataObjectsInOrder).toList());
        }


    }

   /* public void where(String column, String operator, String value) {
        this.queryArr.add(column + operator + value);
    }

    public void where(ArrayList<Object> conditions) {
        for ( Object condition: conditions ) {

            System.out.println(condition);


            //queryAr.add(entry.getKey() + "=" + entry.getValue());
        }
    }

    }*/
}
