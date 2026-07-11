package com.example.minitasks.web;

import com.example.minitasks.repositories.TaskRepository;
import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ValidationTests {

    @WebMvcTest(TaskControllerV3.class)
    static class V3ValidationTest {
        @Autowired MockMvc mvc;
        @MockitoBean TaskRepository repo;

        @Test @DisplayName("V3: POST vacío devuelve 400 y detalle de campo 'title'")
        void v3_post_invalid_returns400() throws Exception {
            mvc.perform(post("/v3/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\"}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Validation failed"))
               .andExpect(jsonPath("$.fields[0].field").value("title"));
        }
    }

    @WebMvcTest(TaskControllerV4.class)
    static class V4ValidationTest {
        @Autowired MockMvc mvc;
        @MockitoBean TaskService service;

        @Test @DisplayName("V4: POST vacío devuelve 400 y detalle de campo 'title'")
        void v4_post_invalid_returns400() throws Exception {
            mvc.perform(post("/v4/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"   \"}"))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.error").value("Validation failed"))
               .andExpect(jsonPath("$.fields[0].field").value("title"));
        }
    }
}
