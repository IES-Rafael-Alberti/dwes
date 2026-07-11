package com.example.minitasks.web;

import com.example.minitasks.entities.Task;
import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV4.class)
class TaskControllerV4PutUpdateTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test
    void put_updates_and_returns200_with_body() throws Exception {
        Task updated = new Task("Nueva"); updated.setId(7L); updated.setDone(true);
        given(service.update(eq(7L), any())).willReturn(updated);

        mvc.perform(put("/v4/tasks/7")
           .contentType(MediaType.APPLICATION_JSON)
           .content("{\"title\":\"Nueva\",\"done\":true}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(7))
           .andExpect(jsonPath("$.title").value("Nueva"))
           .andExpect(jsonPath("$.done").value(true));
    }
}
