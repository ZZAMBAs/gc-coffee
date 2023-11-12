package com.example.gccoffee.service.product;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {
    Product createProduct(String productName, Category category, long price, String description);
    Product updateProduct(UUID productId, String productName, Category category, long price, String description);
    List<Product> getProductsByCategory(Category category);
    Optional<Product> getProductById(UUID productId);
    List<Product> getAllProducts();
    void deleteProductById(UUID productId);
    void deleteAllProducts();
}
