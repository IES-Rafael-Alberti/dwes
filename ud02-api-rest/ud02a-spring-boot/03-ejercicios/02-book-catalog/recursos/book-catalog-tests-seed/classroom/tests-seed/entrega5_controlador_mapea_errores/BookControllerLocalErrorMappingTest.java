package com.example.catalog;

import com.example.catalog.service.BookService;
import com.example.catalog.service.BookNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class BookControllerLocalErrorMappingTest {

    @Autowired MockMvc mvc;
    @MockBean BookService service;

    @Test @DisplayName("El controlador mapea BookNotFoundException → 404 (sin advice global)")
    void controller_maps_not_found_to_404() throws Exception {
        willThrow(new BookNotFoundException("Book not found")).given(service).get(99L);
        mvc.perform(get("/books/99"))
           .andExpect(status().isNotFound());
    }
}
