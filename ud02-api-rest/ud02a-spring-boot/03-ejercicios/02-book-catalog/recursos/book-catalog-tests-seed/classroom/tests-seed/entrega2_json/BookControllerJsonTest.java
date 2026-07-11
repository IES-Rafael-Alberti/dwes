package com.example.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class BookControllerJsonTest {
    @Autowired MockMvc mvc;

    @Test @DisplayName("GET /books devuelve JSON [] al inicio")
    void list_empty_returns_json_array() throws Exception {
        mvc.perform(get("/books").accept(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
           .andExpect(content().json("[]"));
    }

    @Test @DisplayName("POST /books crea y devuelve JSON del libro")
    void create_returns_created_book_json() throws Exception {
        mvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"isbn":"978-1","title":"DDD","author":"Evans","year":2003}"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").exists())
           .andExpect(jsonPath("$.title").value("DDD"))
           .andExpect(jsonPath("$.author").value("Evans"));
    }
}
