package com.github.megbailey.test.butter;

import com.github.megbailey.butter.ApplicationProperties;
import com.github.megbailey.butter.domain.DataModel;
import com.github.megbailey.butter.domain.SampleObjectModel;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.GAccessException;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
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
	public void getAll() throws Exception {
		SampleObjectModel objectModel = new SampleObjectModel(DataModelTestIntegration.gSpreadsheet);
		List<DataModel> models = objectModel.get();

		SampleObjectModel firstModel = (SampleObjectModel) models.get(0);
		SampleObjectModel lastModel = (SampleObjectModel) models.get(models.size()-1);

		//System.out.println("lastModel " + lastModel.getId() + " " +  models.size());

		Assert.assertEquals((int) firstModel.getId(), 1);
		Assert.assertEquals((int) lastModel.getId(), objectModel.lastInsertedID());
	}


	@Test
	public void createNew() throws Exception {
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
	public void delete() throws Exception {
		SampleObjectModel objectModel = new SampleObjectModel(DataModelTestIntegration.gSpreadsheet);
		objectModel.setName("DeleteTestThisShouldGoAway");
		objectModel.setCode("12345");
		objectModel.setYear(2025);
		objectModel.save();

		objectModel.delete();

		List<List<Object>> dataInserted = DataModelTestIntegration.gSpreadsheet.getWithRange(
				"SampleObjectModel",
				DataModelTestIntegration.gSpreadsheet.getLastInsertedRange()
		);
		Assert.assertNull(dataInserted);
	}

	/*@Test
	public void getFilter() throws Exception {
		String tableName = "SampleObjectImpl";
		String filter = "year=2019";
		mockMvc.perform( MockMvcRequestBuilders
				.get("/api/v1/orm/" + tableName + "?" + filter))
				.andExpect(status().isOk())
				.andExpect(content().json("[" +
						"{\"id\":\"7\",\"class_name\":\"Automata, Computability and Formal Languages\",\"class_code\":\"COMP370\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"8\",\"class_name\":\"Algorithms\",\"class_code\":\"COMP480\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"9\",\"class_name\":\"Embedded Systems\",\"class_code\":\"COMP421\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"10\",\"class_name\":\"Summer Undergraduate Research\",\"class_code\":\"UGRS496\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"11\",\"class_name\":\"Computer Graphics\",\"class_code\":\"COMP350\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"12\",\"class_name\":\"Senior Project I\",\"class_code\":\"COMP491\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"13\",\"class_name\":\"Operating Systems\",\"class_code\":\"COMP310\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"14\",\"class_name\":\"Senior Project II\",\"class_code\":\"COMP492\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}" +
						"]"));
	}

*/

}
