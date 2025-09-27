package com.github.megbailey.test.butter;

import com.github.megbailey.butter.ButterDBManager;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.GoggleAccessException;

import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class GSpreadsheetTest {

    private static GSpreadsheet gSpreadsheet;

    @BeforeClass
    public static void spreadsheetSetup() throws IOException, GoggleAccessException {
		/*
			- Get the client secret json file path and other config from application properties
			- Attempts to authenticate as a ServiceAccount for a Google spreadsheet
		*/
        new ButterDBManager("application.properties");
        gSpreadsheet = ButterDBManager.getDatabase();
    }


    @Test
    public void createSheet() throws BadRequestException, IOException, ResourceNotFoundException {
        List<Object> columnLabelList = new ArrayList<>();
        columnLabelList.add("Column1");
        columnLabelList.add("Column2");
        columnLabelList.add("Column3");
        Integer sheetID = gSpreadsheet.firstOrNewSheet("CreateSheetTest");
        gSpreadsheet.updateRow("CreateSheetTest", columnLabelList);
        Assert.assertNotNull(sheetID);

        List<Object> columns = gSpreadsheet.getWithRange("CreateSheetTest", "A1:C1").get(0);
        Assert.assertEquals(columns.get(0), "Column1");
        Assert.assertEquals(columns.get(1), "Column2");
        Assert.assertEquals(columns.get(2), "Column3");
    }


    @Test
    public void deleteSheet() throws BadRequestException, IOException, ResourceNotFoundException {
        Integer sheetID = gSpreadsheet.firstOrNewSheet("DeleteSheetTest");
        Integer deletedSheetID = gSpreadsheet.deleteGSheet("DeleteSheetTest");

        Assert.assertEquals(sheetID, deletedSheetID);
        Assert.assertThrows(ResourceNotFoundException.class, () -> gSpreadsheet.getWithRange("DeleteSheetTest", "A1:C1"));
    }

}
