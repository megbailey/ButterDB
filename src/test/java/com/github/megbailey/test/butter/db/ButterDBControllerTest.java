package com.github.megbailey.test.butter.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.megbailey.butter.db.ButterDBController;
import com.github.megbailey.butter.domain.SampleObjectImpl;
import com.github.megbailey.test.butter.ButterDBTestConfiguration;
import com.github.megbailey.test.butter.TestConfiguration;
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


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@AutoConfigureMockMvc
@ContextConfiguration(classes={
        ButterDBTestConfiguration.class,
        TestConfiguration.class,
})
@SpringBootTest(classes=ButterDBController.class)
public class ButterDBControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /*
        TODO:
            - create a before to bring up a table
            - create an after to teardown a table
     */
    @Test
    public void getIndex() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/"));
        resultActions.andExpect(status().isOk()).andExpect(content().string("Welcome to ButterDB!"));
    }

}
