package com.example.minitasks.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerV2Test {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new TaskControllerV2()).setValidator(validator).build();
    }

    @Test
    void listsTasks() throws Exception {
        mvc.perform(get("/v2/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createsTaskWithLocation() throws Exception {
        mvc.perform(post("/v2/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Write tests\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v2/tasks/1"))
                .andExpect(jsonPath("$.title").value("Write tests"));
    }

    @Test
    void getsExistingTask() throws Exception {
        mvc.perform(post("/v2/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Read\"}"));

        mvc.perform(get("/v2/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Read"));
    }

    @Test
    void returnsNotFoundForMissingTask() throws Exception {
        mvc.perform(get("/v2/tasks/99")).andExpect(status().isNotFound());
    }

    @Test
    void rejectsBlankTitle() throws Exception {
        mvc.perform(post("/v2/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\" \"}"))
                .andExpect(status().isBadRequest());
    }
}
