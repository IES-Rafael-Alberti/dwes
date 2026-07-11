package com.example.minitasks.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV2.class)
class TaskControllerV2Test {
    @Autowired MockMvc mvc;

    @Test void sample_returnsMessageAndTime() throws Exception {
        mvc.perform(get("/v2/sample"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.message").value("hola"))
           .andExpect(jsonPath("$.time").exists());
    }
}
