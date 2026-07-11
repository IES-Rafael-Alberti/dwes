package com.example.minitasks.web;

import com.example.minitasks.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({TaskControllerV3.class, ApiExceptionHandler.class})
class ApiExceptionHandlerV3NotFoundTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskRepository repo;

    @Test void toggle_nonExisting_returns404_json() throws Exception {
        given(repo.findById(999L)).willReturn(Optional.empty());

        mvc.perform(patch("/v3/tasks/999/toggle"))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error").value("Task not found"));
    }
}
