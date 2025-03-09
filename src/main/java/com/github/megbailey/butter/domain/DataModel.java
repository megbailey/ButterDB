package com.github.megbailey.butter.domain;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.megbailey.butter.google.GSheet;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class DataModel implements Serializable {
    private GSpreadsheet spreadsheet;
    private GSheet sheet;
    private Field[] modelFields;
    private ArrayList<String> queryArr;

    public DataModel(GSpreadsheet spreadsheet) {
        this.queryArr = new ArrayList<>();
        this.spreadsheet = spreadsheet;
        // get the field names currently defined in the model's class
        this.modelFields = this.getDeclaredFieldList( this.getClass() );
        List<Object> fieldNameList = new ArrayList<>(modelFields.length);
        for (Field fieldForModel: modelFields) {
            fieldNameList.add(fieldForModel.getName());
        }

       try {
            System.out.println("first or new " + this.getClass().getSimpleName()+ " with " + this.modelFields);
            // Create a new sheet for this model if it does not exist and update column names to be synced with model
            this.sheet = this.spreadsheet.firstOrNewSheet( this.getClass().getSimpleName(), fieldNameList );

       } catch (IOException | BadRequestException e) {
           e.printStackTrace();
           System.exit(-1);
       }
    }

    private Field[] getDeclaredFieldList(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
        }
        return fields;
    }

    public void save() throws IllegalAccessException, BadRequestException, IOException, ResourceNotFoundException {
        if ( this.modelFields == null ) {
            this.modelFields = this.getDeclaredFieldList( this.getClass() );
        }

        List<Object> dataObjects = new ArrayList<>(this.modelFields.length);
        for (Field fieldInModel: modelFields) {
            fieldInModel.setAccessible(true);
            dataObjects.add( fieldInModel.get(this) );
            System.out.println( "Field name " + fieldInModel.getName() + ". Field Value " + fieldInModel.get(this));
        }
        this.spreadsheet.updateRow(this.sheet.getName(), dataObjects);

    }
    public void where(String column, String operator, String value) {
        this.queryArr.add(column + operator + value);
    }

    public void where(ArrayList<Object> conditions) {
        for ( Object condition: conditions ) {

            System.out.println(condition);


            //queryAr.add(entry.getKey() + "=" + entry.getValue());
        }
    }

    public List<String> toList() {
        Type type = this.getClass(); // MyClass is static class with static properties
       /* foreach (var p in type.GetProperties())
        {
            var v = p.GetValue(null, null); // static classes cannot be instanced, so use null...
        }

        List<String> values = new ArrayList<>(2);
        values.add( getId().toString() );
        values.add( getName() );
        values.add( getCode() );
        values.add( getYear().toString() );
        return values;*/
        return new ArrayList<>(2);

    }
}
