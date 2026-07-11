package com.example.minitasks.web;

import com.example.minitasks.entities.Task;
import com.example.minitasks.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskControllerV4.class)
class TaskControllerV4PageableHeadersTest {

    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test
    void list_withPaging_addsHeadersAndCallsService() throws Exception {
        List<Task> content = List.of(new Task("A"), new Task("B"));
        content.get(0).setId(1L);
        content.get(1).setId(2L);
        Page<Task> page = new PageImpl<>(content, PageRequest.of(0, 2), 5);
        given(service.listPage(isNull(), isNull(), any(Pageable.class))).willReturn(page);

        mvc.perform(get("/v4/tasks?page=0&size=2&sort=title,asc"))
           .andExpect(status().isOk())
           .andExpect(header().string("X-Total-Count", "5"))
           .andExpect(header().string(org.springframework.http.HttpHeaders.LINK, org.hamcrest.Matchers.containsString("rel=\"next\"")))
           .andExpect(jsonPath("$[0].title").value("A"));

        verify(service).listPage(isNull(), isNull(), any(Pageable.class));
    }
}
