package com.github.megbailey.test.butter;

import com.github.megbailey.butter.Model;


public class SampleObjectModel extends Model {
    public static String primaryKey = "id";
    public static String[] fields = new String[]{ "id", "name", "code", "year", "test" };

    public SampleObjectModel() {
        super(primaryKey, fields, true);
    }

}
