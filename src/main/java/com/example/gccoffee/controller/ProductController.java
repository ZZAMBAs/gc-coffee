package com.example.gccoffee.controller;

import com.example.gccoffee.controller.dto.ProductRequest;
import com.example.gccoffee.exception.EntityNotFoundException;
import com.example.gccoffee.model.Product;
import com.example.gccoffee.service.product.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        List<Product> products = productService.getAllProducts();

        model.addAttribute("products", products);

        return "product-list";
    }

    @GetMapping("/products/{productId}")
    public String showProductDetails(@PathVariable UUID productId, Model model) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 Product가 없습니다."));

        model.addAttribute("product", product);

        return "product-details";
    }

    @GetMapping("/new-product")
    public String showCreatePage() {
        return "new-product";
    }

    @PostMapping("/products")
    public String createProduct(ProductRequest productRequest) {
        productService.createProduct(productRequest.productName(),
                productRequest.category(),
                productRequest.price(),
                productRequest.description());
        return "redirect:/products";
    }

    @PostMapping("/products/{productId}")
    public String updateProduct(ProductRequest productRequest, @PathVariable UUID productId) {
        productService.updateProduct(productId,
                productRequest.productName(),
                productRequest.category(),
                productRequest.price(),
                productRequest.description());

        return "redirect:/products";
    }
}
