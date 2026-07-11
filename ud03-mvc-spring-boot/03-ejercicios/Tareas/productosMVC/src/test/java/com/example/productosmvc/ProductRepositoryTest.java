package com.example.productosmvc;

import com.example.productosmvc.model.Product;
import com.example.productosmvc.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAllInBatch();
    }

    @Test
    public void testFindAllProducts() {
        // Crear y guardar productos
        Product product1 = new Product();
        product1.setName("Producto 1");
        product1.setPrice(10.0);

        Product product2 = new Product();
        product2.setName("Producto 2");
        product2.setPrice(20.0);

        productRepository.save(product1);
        productRepository.save(product2);

        // Validar los productos guardados
        List<Product> products = productRepository.findAll();
        assertEquals(2, products.size());
    }
}
