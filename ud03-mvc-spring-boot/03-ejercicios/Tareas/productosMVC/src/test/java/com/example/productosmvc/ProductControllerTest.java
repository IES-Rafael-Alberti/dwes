package com.example.productosmvc;

import com.example.productosmvc.controller.ProductController;
import com.example.productosmvc.model.Product;
import com.example.productosmvc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    public void testGetAllProducts() throws Exception {
        // Mock de productos
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Producto 1");
        product1.setPrice(10.0);
        product1.setDescription("Descripción 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Producto 2");
        product2.setPrice(20.0);
        product2.setDescription("Descripción 2");

        when(productService.getAllProducts()).thenReturn(Arrays.asList(product1, product2));

        // Validación de respuesta
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("products"))
                .andExpect(view().name("products"));
    }
}
