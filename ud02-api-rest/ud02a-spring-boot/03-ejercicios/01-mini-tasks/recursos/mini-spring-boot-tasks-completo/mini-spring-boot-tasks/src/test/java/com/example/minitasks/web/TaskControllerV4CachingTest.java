package com.example.minitasks.web;

import com.example.minitasks.entities.Task;
import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV4.class)
class TaskControllerV4CachingTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test void list_setsEtag_andCacheControl() throws Exception {
        Task t1 = new Task("A"); t1.setId(1L);
        Task t2 = new Task("B"); t2.setId(2L);
        given(service.list(null)).willReturn(List.of(t1, t2));

        mvc.perform(get("/v4/tasks"))
           .andExpect(status().isOk())
           .andExpect(header().exists("ETag"))
           .andExpect(header().string("Cache-Control", org.hamcrest.Matchers.containsString("max-age")));
    }

    @Test void list_returns304_when_IfNoneMatch_matches() throws Exception {
        String expectedEtag = "\"tasks-2\"";
        given(service.list(null)).willReturn(List.of(new Task("A"), new Task("B")));

        mvc.perform(get("/v4/tasks").header("If-None-Match", expectedEtag))
           .andExpect(status().isNotModified());
    }
}
