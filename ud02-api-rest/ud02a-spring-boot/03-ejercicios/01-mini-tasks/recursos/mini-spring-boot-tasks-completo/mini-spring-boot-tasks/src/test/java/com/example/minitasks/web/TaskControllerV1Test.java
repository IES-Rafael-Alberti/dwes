package com.example.minitasks.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV1.class)
class TaskControllerV1Test {
    @Autowired MockMvc mvc;

    @Test void ping_returnsPong() throws Exception {
        mvc.perform(get("/v1/ping"))
           .andExpect(status().isOk())
           .andExpect(content().string("pong"));
    }

    @Test void create_returns201_and_body() throws Exception {
        mvc.perform(post("/v1/tasks").contentType(MediaType.APPLICATION_JSON)
           .content("{\"title\":\"Test V1\"}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").exists())
           .andExpect(jsonPath("$.title").value("Test V1"))
           .andExpect(jsonPath("$.done").value(false));
    }
}
