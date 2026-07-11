package com.example.catalog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class BookControllerResponseEntityTest {

    @Autowired MockMvc mvc;

    @Test @DisplayName("POST /books → 201 Created + Location")
    void post_returns_201_with_location() throws Exception {
        mvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"isbn":"978-1","title":"Refactoring","author":"Fowler","year":1999}"))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", containsString("/books/")));
    }

    @Test @DisplayName("DELETE /books/{id} → 204 No Content")
    void delete_returns_204() throws Exception {
        mvc.perform(delete("/books/1"))
           .andExpect(status().isNoContent())
           .andExpect(content().string(""));
    }
}
