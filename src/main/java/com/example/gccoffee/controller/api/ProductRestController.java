package com.example.gccoffee.controller.api;

import com.example.gccoffee.controller.dto.ProductRequest;
import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import com.example.gccoffee.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class ProductRestController {
    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/v1/products")
    public List<Product> getProducts(@RequestParam Optional<Category> category) {
        return category.map(productService::getProductsByCategory)
                .orElseGet(productService::getAllProducts);
    }

    @GetMapping("api/v1/products/{productId}")
    public Product getProductById(@PathVariable UUID productId) {
        return productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("{0}에 해당하는 상품이 존재하지 않습니다.", productId)));
    }

    @PostMapping("/api/v1/products")
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest productRequest) {
        Product product = productService.createProduct(productRequest.productName(),
                productRequest.category(),
                productRequest.price(),
                productRequest.description() != null ? productRequest.description() : "");
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/api/v1/products")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductRequest productRequest) {
        Product updatedProduct = productService.updateProduct(productRequest.productId(),
                productRequest.productName(),
                productRequest.category(),
                productRequest.price(),
                productRequest.description());

        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/api/v1/products/{productId}")
    public void deleteProductById(@PathVariable UUID productId) {
        log.info(productId.toString());

        productService.deleteProductById(productId);
    }

    @DeleteMapping("/api/v1/products")
    public void deleteProductAll() {
        productService.deleteAllProducts();
    }
}
