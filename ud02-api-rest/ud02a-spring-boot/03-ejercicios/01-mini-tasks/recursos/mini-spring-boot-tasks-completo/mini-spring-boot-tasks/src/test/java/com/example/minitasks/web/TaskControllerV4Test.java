package com.example.minitasks.web;

import com.example.minitasks.entities.Task;
import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV4.class)
class TaskControllerV4Test {
    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test void list_ok() throws Exception {
        Task t = new Task("Service"); t.setId(1L);
        given(service.list(null)).willReturn(List.of(t));

        mvc.perform(get("/v4/tasks"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].title").value("Service"));
    }

    @Test void create_returns201() throws Exception {
        Task saved = new Task("Created"); saved.setId(3L);
        given(service.create(org.mockito.ArgumentMatchers.any())).willReturn(saved);

        mvc.perform(post("/v4/tasks").contentType(MediaType.APPLICATION_JSON)
           .content("{\"title\":\"Created\"}"))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/v4/tasks/3")))
           .andExpect(jsonPath("$.id").value(3L));
    }

    @Test void delete_returns204() throws Exception {
        mvc.perform(delete("/v4/tasks/7"))
           .andExpect(status().isNoContent());
        verify(service).delete(7L);
    }
}
