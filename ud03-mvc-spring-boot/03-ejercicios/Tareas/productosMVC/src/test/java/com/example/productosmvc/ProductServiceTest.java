package com.example.productosmvc;


import com.example.productosmvc.model.Product;
import com.example.productosmvc.repository.ProductRepository;
import com.example.productosmvc.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    public ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetProductById() {
        // Mock de producto
        Product product = new Product();
        product.setId(1L);
        product.setName("Producto Test");
        product.setPrice(15.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Validación
        Product result = productService.getProductById(1L);
        assertEquals("Producto Test", result.getName());
        assertEquals(15.0, result.getPrice());
    }

    @Test
    public void testSaveProduct() {
        // Mock de producto
        Product product = new Product();
        product.setId(1L);
        product.setName("Producto Test");
        product.setPrice(15.0);

        when(productRepository.save(product)).thenReturn(product);

        // Guardar producto
        productService.saveProduct(product);

        // Verificar que el método del repositorio fue llamado
        verify(productRepository, times(1)).save(product);
    }
}
