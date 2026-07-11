package com.example.minitasks.web;

import com.example.minitasks.entities.Task;
import com.example.minitasks.repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV3.class)
class TaskControllerV3Test {

    @Autowired MockMvc mvc;
    @MockitoBean TaskRepository repo;

    @Test void list_returnsFromRepository() throws Exception {
        Task t1 = new Task("A"); t1.setId(1L);
        Task t2 = new Task("B"); t2.setId(2L);
        given(repo.findAll()).willReturn(List.of(t1, t2));

        mvc.perform(get("/v3/tasks"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].title").value("A"))
           .andExpect(jsonPath("$[1].title").value("B"));
    }

    @Test void create_persists_and_returnsEntity() throws Exception {
        Task saved = new Task("Saved"); saved.setId(10L);
        given(repo.save(any(Task.class))).willReturn(saved);

        mvc.perform(post("/v3/tasks").contentType(MediaType.APPLICATION_JSON)
           .content("{\"title\":\"Saved\"}"))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(10L))
           .andExpect(jsonPath("$.title").value("Saved"));
    }

    @Test void toggle_flipsDone() throws Exception {
        Task t = new Task("Toggle"); t.setId(5L); t.setDone(false);
        given(repo.findById(5L)).willReturn(Optional.of(t));

        mvc.perform(patch("/v3/tasks/5/toggle"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.done").value(true));
    }
}
