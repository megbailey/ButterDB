package com.github.megbailey.test.butter;

import com.github.megbailey.butter.ApplicationProperties;
import com.github.megbailey.butter.domain.DataModel;
import com.github.megbailey.butter.domain.SampleObjectModel;
import com.github.megbailey.butter.google.GSpreadsheet;
import com.github.megbailey.butter.google.api.GAuthentication;
import com.github.megbailey.butter.google.exception.GAccessException;

import com.google.gson.JsonArray;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.BeforeClass;
import org.junit.Test;

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

		Assert.assertEquals((int) firstModel.getId(), 1);
		Assert.assertEquals((int) lastModel.getId(), models.size());

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

		Assert.assertEquals( "createNewTest", dataInserted.get(1) );
		Assert.assertEquals( "123", dataInserted.get(2) );
		Assert.assertEquals( "2025", dataInserted.get(3) );
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

	@Test
	public void createAndDeleteObject() throws Exception {
		String tableName = "SampleObjectImpl";
		SampleObjectImpl testObject = new SampleObjectImpl()
				.setId(15)
				.setName("Test class 1")
				.setCode("COMP100")
				.setYear(2022);

		ObjectMapper mapper = new ObjectMapper();
		List<String> content = new ArrayList<>();
		content.add(mapper.writeValueAsString(testObject));
		String jsonStr = String.join(",\n", content);
		jsonStr = "[\n" + jsonStr + "\n]";

		// Create
		mockMvc.perform( MockMvcRequestBuilders
				.post("/api/v1/orm/" + tableName + "/create")
					.content( jsonStr )
					.contentType( MediaType.APPLICATION_JSON_VALUE )
					.accept( MediaType.APPLICATION_JSON_VALUE )
					.characterEncoding( Charset.defaultCharset() ))
				.andExpect( status().isCreated() );

		// Successful create
		String filter = "year=2022";
		mockMvc.perform( MockMvcRequestBuilders
						.get("/api/v1/orm/" + tableName + "?" + filter))
				.andExpect(status().isOk())
				.andExpect(content().json("[" +
					"{\"id\":\"15\",\"class_name\":\"Test class 1\",\"class_code\":\"COMP100\",\"year\":\"2022\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}" +
					"]"));

		// Delete
		filter = "id=15";
		mockMvc.perform( MockMvcRequestBuilders
						.delete("/api/v1/orm/" + tableName + "/delete?" + filter)
						.characterEncoding( Charset.defaultCharset() ))
				.andExpect( status().isOk() );

		// Successful delete
		filter = "year=2022";
		mockMvc.perform( MockMvcRequestBuilders
						.get("/api/v1/orm/" + tableName + "?" + filter))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));

	}
*/

}
