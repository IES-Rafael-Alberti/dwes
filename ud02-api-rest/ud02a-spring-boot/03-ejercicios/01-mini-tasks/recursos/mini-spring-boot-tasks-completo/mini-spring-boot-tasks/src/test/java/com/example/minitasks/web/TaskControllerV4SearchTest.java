package com.example.minitasks.web;

import com.example.minitasks.entities.Task;
import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV4.class)
class TaskControllerV4SearchTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test
    void search_q_param_filters() throws Exception {
        List<Task> content = List.of(new Task("Aprender Spring"));
        content.get(0).setId(10L);
        Page<Task> page = new PageImpl<>(content);

        given(service.listPage(isNull(), eq("spring"), any(Pageable.class))).willReturn(page);

        mvc.perform(get("/v4/tasks?page=0&size=10&q=spring"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].title").value("Aprender Spring"));
    }
}
