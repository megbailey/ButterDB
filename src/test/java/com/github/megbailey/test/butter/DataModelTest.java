package com.github.megbailey.test.butter;

import com.github.megbailey.butter.ButterDBManager;
import com.github.megbailey.butter.Model;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.SystemErrorException;
import com.github.megbailey.butter.google.exception.GoggleAccessException;

import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class DataModelTest {


	@BeforeClass
	public static void spreadsheetSetup() throws IOException, GoggleAccessException {
		/*
			- Get the client secret json file path and other config from application properties
			- Attempts to authenticate as a ServiceAccount for a Google spreadsheet
		*/
		new ButterDBManager("application.properties");
	}


	@Test
	public void createModel() throws BadRequestException, ResourceNotFoundException {
		SampleObjectModel model = new SampleObjectModel();
		model.setFieldValue("name", "testValue");
		model.save();
		Assert.assertNotNull(model.getFieldValue("id"));
	}

	@Test
	public void findModel() throws SystemErrorException, IOException, GoggleAccessException, ResourceNotFoundException {
		SampleObjectModel model = new SampleObjectModel();
		Model result = model.find(101);
		Assert.assertEquals( 101, result.getFieldValue("id"));
		Assert.assertEquals( "testValue", result.getFieldValue("name"));
	}

	@Test
	public void getWhere() throws Exception {
		SampleObjectModel model = new SampleObjectModel();
		Model result = model.where("name", "=", "testValue").get();
		Assert.assertEquals( "testValue", result.getFieldValue("name"));
	}

	// TODO: Rewrite as a collection
	@Test
	public void getOrWhere() throws Exception {
		SampleObjectModel model = new SampleObjectModel();
		Model result = model
				.where("name", "=", "testValue")
				.orWhere("name", "=", "createNewTest")
				.get();
		Object value = result.getFieldValue("name");

		if ( "createNewTest".equals(value) ) {
			Assert.assertEquals("createNewTest", value);
		} else if ( "testValue".equals(value) ) {
			Assert.assertEquals( "testValue", value);
		} else {
			Assert.fail();
		}
	}

	/*@Test
	public void deleteSheet() throws BadRequestException, IOException, ResourceNotFoundException {
		GSheet sheet = DataModelIntegrationTest.gSpreadsheet.firstOrNewSheet(
				"DeleteSheetTest",
				null
		);
		DataModelIntegrationTest.gSpreadsheet.deleteGSheet("DeleteSheetTest");

		Assert.assertThrows(ResourceNotFoundException.class, ()->{
			DataModelIntegrationTest.gSpreadsheet.getWithRange(sheet.getName(), "A1:C1");
		});
	}*/




	/*@Test
	public void getAll() throws Exception {
		/*SampleObjectModel objectModel = new SampleObjectModel(DataModelIntegrationTest.gSpreadsheet);
		objectModel.setName("getAllTest");
		objectModel.setCode("1234");
		objectModel.setYear(2027);
		objectModel.save();

		List<Object> models = objectModel.get();
		SampleObjectModel firstModel = (SampleObjectModel) models.get(0);
		SampleObjectModel lastModel = (SampleObjectModel) models.get(models.size()-1);

		*//*System.out.println("firstModel " + firstModel.getId() + " " +  1);
		System.out.println("lastModel " + lastModel.getId() + " " +  models.size());*//*

		Assert.assertEquals((int) firstModel.getId(), 1);
		Assert.assertEquals((int) lastModel.getId(), objectModel.lastInsertedID());
	}*/


	/*@Test
	public void delete() throws Exception {
		SampleObjectModel objectModel = new SampleObjectModel(DataModelIntegrationTest.gSpreadsheet);
		objectModel.setName("DeleteTestThisShouldGoAway");
		objectModel.setCode("12345");
		objectModel.setYear(2025);
		objectModel.save();

		List<List<Object>> dataInserted = DataModelIntegrationTest.gSpreadsheet.getWithRange(
				"SampleObjectModel",
				DataModelIntegrationTest.gSpreadsheet.getLastInsertedRange()
		);

		Assert.assertEquals(objectModel.getId().toString(), dataInserted.get(0).get(0) );

		objectModel.delete();
		Thread.sleep(2000);
		List<List<Object>> dataDeleted = DataModelIntegrationTest.gSpreadsheet.getWithRange(
				"SampleObjectModel",
				DataModelIntegrationTest.gSpreadsheet.getLastInsertedRange()
		);
		Assert.assertNull(dataDeleted);
	}*/
}
