package com.example.catalog;

import com.example.catalog.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class BookControllerServiceDelegationTest {

    @Autowired MockMvc mvc;
    @MockBean BookService service;

    @Test @DisplayName("El controlador delega en BookService.create(...)")
    void controller_delegates_to_service_create() throws Exception {
        mvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"isbn":"978-1","title":"Clean Code","author":"Martin","year":2008}"))
           .andExpect(status().isCreated());
        verify(service).create(any());
    }
}
