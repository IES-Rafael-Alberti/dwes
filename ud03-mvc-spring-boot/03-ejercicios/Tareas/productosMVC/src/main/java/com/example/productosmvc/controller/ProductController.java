package com.example.productosmvc.controller;

import com.example.productosmvc.model.Product;
import com.example.productosmvc.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    // Logger para registrar información
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        logger.info("Listando todos los productos.");
        // Registro de información
        products.forEach(product -> {
            logger.info("Producto - ID: {}, Nombre: {}, Precio: {}, Descripción: {}",
                    product.getId(), product.getName(), product.getPrice(), product.getDescription());
        });

        products.forEach(System.out::println); // Verifica el contenido
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping("/new")
    public String showProductForm(Model model) {
        Product defaultProduct = new Product();
        defaultProduct.setName("Producto por defecto"); // Valor por defecto
        defaultProduct.setPrice(0.0);                  // Valor por defecto
        defaultProduct.setDescription("Sin descripción"); // Valor por defecto
        logger.info("Mostrando formulario para crear un nuevo producto.");
        // Registro de información
        logger.info("Producto por defecto: {}", defaultProduct); // Registro de información
        model.addAttribute("product", defaultProduct);
        return "product-form";
    }


    @PostMapping
    public String saveProduct(@ModelAttribute("product") Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product-form";
    }

    @PostMapping("/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}
