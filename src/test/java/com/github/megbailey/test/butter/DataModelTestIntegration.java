package com.github.megbailey.test.butter;

import com.github.megbailey.butter.ApplicationProperties;
import com.github.megbailey.butter.domain.DataModel;
import com.github.megbailey.butter.domain.SampleObjectModel;
import com.github.megbailey.butter.google.GSheet;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.GAccessException;

import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class DataModelTestIntegration {
	private static GSpreadsheet gSpreadsheet;

	@BeforeClass
	public static void spreadsheetSetup() throws IOException, GAccessException {
		ApplicationProperties properties = new ApplicationProperties("application.properties");
		/*  Get the client secret json file path from application properties */
		GAuthentication gAuthentication = GAuthentication.getInstance(properties.getCredentials());
		/*  Get the spreadsheet id from application properties */
		gAuthentication.setSpreadsheetID(properties.getSpreadsheetID())
				.authenticateWithServiceAccount();

		DataModelTestIntegration.gSpreadsheet =  new GSpreadsheet(gAuthentication);
	}
	@Test
	public void createSheet() throws BadRequestException, IOException, ResourceNotFoundException {
		List<Object> columnLabelList = new ArrayList<>();
		columnLabelList.add("Column1");
		columnLabelList.add("Column2");
		columnLabelList.add("Column3");
		GSheet sheet = DataModelTestIntegration.gSpreadsheet.firstOrNewSheet(
				"CreateSheetTest",
				columnLabelList
		);
		Assert.assertEquals(sheet.getName(), "CreateSheetTest");
		Assert.assertNotNull(sheet.getID());

		List<Object> columns = DataModelTestIntegration.gSpreadsheet.getWithRange(sheet.getName(), "A1:C1").get(0);
		Assert.assertEquals(columns.get(0), "Column1");
		Assert.assertEquals(columns.get(1), "Column2");
		Assert.assertEquals(columns.get(2), "Column3");
	}

	@Test
	public void deleteSheet() throws BadRequestException, IOException, ResourceNotFoundException {
		GSheet sheet = DataModelTestIntegration.gSpreadsheet.firstOrNewSheet(
				"DeleteSheetTest",
				null
		);
		DataModelTestIntegration.gSpreadsheet.deleteGSheet("DeleteSheetTest");

		Assert.assertThrows(ResourceNotFoundException.class,
				()->{
					DataModelTestIntegration.gSpreadsheet.getWithRange(sheet.getName(), "A1:C1").get(0);
				});


	}

	@Test
	public void createNewModel() throws Exception {
		SampleObjectModel objectModel = new SampleObjectModel(DataModelTestIntegration.gSpreadsheet);
		objectModel.setName("createNewTest");
		objectModel.setCode("123");
		objectModel.setYear(2025);
		objectModel.save();

		List<Object> dataInserted = DataModelTestIntegration.gSpreadsheet.getWithRange(
				"SampleObjectModel",
				DataModelTestIntegration.gSpreadsheet.getLastInsertedRange()
		).get(0);

		/*System.out.println(
				"From created " + objectModel.getId() + "\n" +
				"From fetch " + dataInserted.get(0) + "\n" +
				DataModelTestIntegration.gSpreadsheet.getLastInsertedRange()
		);*/

		Assert.assertEquals(objectModel.getId().toString(), dataInserted.get(0) );
		Assert.assertEquals( "createNewTest", dataInserted.get(1) );
		Assert.assertEquals( "123", dataInserted.get(2) );
		Assert.assertEquals( "2025", dataInserted.get(3) );
	}

	@Test
	public void getAll() throws Exception {
		SampleObjectModel objectModel = new SampleObjectModel(DataModelTestIntegration.gSpreadsheet);
		objectModel.setName("getAllTest");
		objectModel.setCode("1234");
		objectModel.setYear(2027);
		objectModel.save();

		List<DataModel> models = objectModel.get();

		SampleObjectModel firstModel = (SampleObjectModel) models.get(0);
		SampleObjectModel lastModel = (SampleObjectModel) models.get(models.size()-1);

		/*System.out.println("firstModel " + firstModel.getId() + " " +  1);
		System.out.println("lastModel " + lastModel.getId() + " " +  models.size());*/

		Assert.assertEquals((int) firstModel.getId(), 1);
		Assert.assertEquals((int) lastModel.getId(), objectModel.lastInsertedID());
	}




	@Test
	public void delete() throws Exception {
		SampleObjectModel objectModel = new SampleObjectModel(DataModelTestIntegration.gSpreadsheet);
		objectModel.setName("DeleteTestThisShouldGoAway");
		objectModel.setCode("12345");
		objectModel.setYear(2025);
		objectModel.save();

		List<List<Object>> dataInserted = DataModelTestIntegration.gSpreadsheet.getWithRange(
				"SampleObjectModel",
				DataModelTestIntegration.gSpreadsheet.getLastInsertedRange()
		);

		Assert.assertEquals(objectModel.getId().toString(), dataInserted.get(0).get(0) );

		objectModel.delete();
		Thread.sleep(2000);
		List<List<Object>> dataDeleted = DataModelTestIntegration.gSpreadsheet.getWithRange(
				"SampleObjectModel",
				DataModelTestIntegration.gSpreadsheet.getLastInsertedRange()
		);
		Assert.assertNull(dataDeleted);
	}

	@Test
	public void getWhere() throws Exception {

	}



}
