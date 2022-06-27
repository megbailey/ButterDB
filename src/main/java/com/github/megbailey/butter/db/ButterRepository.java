package com.github.megbailey.butter.db;

import com.github.megbailey.google.GSpreadsheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;


@Repository
public class ButterRepository {

    private final GSpreadsheet gSpreadsheet;

    @Autowired
    public ButterRepository(GSpreadsheet gSpreadsheet) {
        this.gSpreadsheet = gSpreadsheet;
    }



    public boolean createGSheet(String tableName) throws IOException {
        return this.gSpreadsheet.createGSheet(tableName);
    }


    public boolean deleteGSheet(String tableName) throws IOException {
        return this.gSpreadsheet.deleteGSheet(tableName);
    }


}
