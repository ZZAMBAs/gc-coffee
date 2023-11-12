package com.example.gccoffee.service.product;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import com.example.gccoffee.repository.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultProductService implements ProductService {
    private final ProductRepository productRepository;

    public DefaultProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    @Override
    public Optional<Product> getProductById(UUID productId) {
        return productRepository.findById(productId);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void deleteProductById(UUID productId) {
        productRepository.deleteById(productId);
    }

    @Override
    public void deleteAllProducts() {
        productRepository.deleteAll();
    }

    @Override
    public Product createProduct(String productName, Category category, long price, String description) {
        Product product = new Product(UUID.randomUUID(),
                productName,
                category,
                price,
                description,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        return productRepository.insert(product);
    }

    @Override
    public Product updateProduct(UUID productId, String productName, Category category, long price, String description) {
        return productRepository.update(new Product(productId, productName, category, price, description));
    }
}
