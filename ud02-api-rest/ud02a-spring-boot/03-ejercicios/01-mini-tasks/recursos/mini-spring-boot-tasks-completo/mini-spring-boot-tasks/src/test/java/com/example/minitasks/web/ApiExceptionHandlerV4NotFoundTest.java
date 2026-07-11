package com.example.minitasks.web;

import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({TaskControllerV4.class, ApiExceptionHandler.class})
class ApiExceptionHandlerV4NotFoundTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test void delete_nonExisting_returns404_json() throws Exception {
        willThrow(new IllegalArgumentException("Task not found")).given(service).delete(12345L);

        mvc.perform(delete("/v4/tasks/12345"))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error").value("Task not found"));
    }
}
