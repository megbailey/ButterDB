package com.github.megbailey.test.butter.table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.megbailey.butter.domain.SampleObjectImpl;
import com.github.megbailey.butter.db.ButterTableController;
import com.github.megbailey.test.butter.ButterDBTestConfiguration;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes={ButterDBTestConfiguration.class})
@SpringBootTest(classes=ButterTableController.class)
public class ButterTableControllerTest {

	@Autowired
	private MockMvc mockMvc;

	/*
		TODO:
			- create a before to bring up a table
			- create an after to teardfown a table
	 */
	@Test
	public void getIndex() throws Exception {
		ResultActions resultActions = this.mockMvc.perform( MockMvcRequestBuilders.get( "/api/v1/orm/" ) );
		resultActions.andExpect( status().isOk() ).andExpect( content().string("GSheet ORM index.") );
	}

	@Test
	public void getAll() throws Exception {
		String tableName = "SampleObjectImpl";
		ResultActions resultActions = this.mockMvc.perform( MockMvcRequestBuilders
				.get("/api/v1/orm/" + tableName) )
				.andExpect( status().isOk() )
				.andExpect( content().json("[" +
						"{\"id\":\"0\",\"class_name\":\"Computer Programming I\",\"class_code\":\"COMP150\",\"year\":\"2016\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"1\",\"class_name\":\"Computer Programming II\",\"class_code\":\"COMP151\",\"year\":\"2017\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"2\",\"class_name\":\"Introduction to Computer Systems\",\"class_code\":\"COMP280\",\"year\":\"2017\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"3\",\"class_name\":\"Data Structures and Algorithms\",\"class_code\":\"COMP285\",\"year\":\"2018\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"4\",\"class_name\":\"Networking\",\"class_code\":\"COMP375\",\"year\":\"2018\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"5\",\"class_name\":\"Principles of Digital Hardware\",\"class_code\":\"COMP300\",\"year\":\"2018\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"6\",\"class_name\":\"Object-Oriented Design and Programming\",\"class_code\":\"COMP305\",\"year\":\"2018\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"7\",\"class_name\":\"Automata, Computability and Formal Languages\",\"class_code\":\"COMP370\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"8\",\"class_name\":\"Algorithms\",\"class_code\":\"COMP480\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"9\",\"class_name\":\"Embedded Systems\",\"class_code\":\"COMP421\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"10\",\"class_name\":\"Summer Undergraduate Research\",\"class_code\":\"UGRS496\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"11\",\"class_name\":\"Computer Graphics\",\"class_code\":\"COMP350\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"12\",\"class_name\":\"Senior Project I\",\"class_code\":\"COMP491\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"13\",\"class_name\":\"Operating Systems\",\"class_code\":\"COMP310\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}," +
						"{\"id\":\"14\",\"class_name\":\"Senior Project II\",\"class_code\":\"COMP492\",\"year\":\"2019\",\"@class\":\"com.github.megbailey.butter.domain.SampleObjectImpl\"}" +
						"]") );
	}


	@Test
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
	public void createObject() throws Exception {

		SampleObjectImpl testObject = new SampleObjectImpl()
				.setId(5)
				.setName("Test class 1")
				.setCode("COMP100")
				.setYear(2022);
		SampleObjectImpl testObject2 = new SampleObjectImpl()
				.setId(6)
				.setName("Test class 2")
				.setCode("COMP101")
				.setYear(2022);
		ObjectMapper mapper = new ObjectMapper();
		List<String> content = new ArrayList<>();
		content.add(mapper.writeValueAsString(testObject));
		content.add(mapper.writeValueAsString(testObject2));
		String jsonStr = String.join(",\n", content);
		jsonStr = "[\n" + jsonStr + "\n]";

		String tableName = "SampleObjectImpl";
		mockMvc.perform( MockMvcRequestBuilders
				.post("/api/v1/orm/" + tableName + "/create")
					.content( jsonStr )
					.contentType( MediaType.APPLICATION_JSON_VALUE )
					.accept( MediaType.APPLICATION_JSON_VALUE )
					.characterEncoding( Charset.defaultCharset() ))
				.andExpect( status().isCreated() );
				//.andExpect(content().json("[);
	}

/*	@Test
	public void deleteObject() throws Exception {


		String tableName = "SampleObjectImpl";
		String filter = "year=2022";
		mockMvc.perform( MockMvcRequestBuilders
						.delete("/api/v1/orm/" + tableName + "/delete?" + filter)
						//.content( jsonStr )
						//.contentType( MediaType.APPLICATION_JSON_VALUE )
						//.accept( MediaType.APPLICATION_JSON_VALUE )
						.characterEncoding( Charset.defaultCharset() ))
				.andExpect( status().isOk() );
		//.andExpect(content().json("[);
	}*/

	public static boolean isJSONValid( String jsonInString ) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			mapper.readTree(jsonInString);
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
