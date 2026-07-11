package com.example.minitasks.web;

import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV4.class)
class TaskControllerV4HeadTasksTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test
    void head_returnsHeaders_only() throws Exception {
        Page<?> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 42);
        given(service.listPage(isNull(), isNull(), any())).willReturn((Page) page);

        mvc.perform(head("/v4/tasks?page=0&size=10"))
           .andExpect(status().isOk())
           .andExpect(header().string("X-Total-Count", "42"))
           .andExpect(header().exists(org.springframework.http.HttpHeaders.LINK))
           .andExpect(content().string(""));
    }
}
