package com.example.catalog;

import com.example.catalog.web.BookController;
import com.example.catalog.web.GlobalExceptionHandler;
import com.example.catalog.service.BookService;
import com.example.catalog.service.BookNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerAdviceGlobalTest {

    @Autowired MockMvc mvc;
    @MockBean BookService service;

    @Test @DisplayName("Advice global mapea BookNotFoundException → 404 con JSON de error")
    void advice_maps_not_found_to_404() throws Exception {
        willThrow(new BookNotFoundException("Book not found")).given(service).get(42L);
        mvc.perform(get("/books/42"))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.error").value("Book not found"));
    }
}
