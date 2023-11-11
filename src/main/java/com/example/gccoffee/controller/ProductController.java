package com.example.gccoffee.controller;

import com.example.gccoffee.controller.dto.ProductRequest;
import com.example.gccoffee.model.Product;
import com.example.gccoffee.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String productPage(Model model) {
        List<Product> products = productService.getAllProducts();

        model.addAttribute("products", products);

        return "product-list";
    }

    @GetMapping("/new-product")
    public String newProductPage() {
        return "new-product";
    }

    @PostMapping("/products")
    public String newProduct(ProductRequest productRequest) {
        productService.createProduct(productRequest.productName(),
                productRequest.category(),
                productRequest.price(),
                productRequest.description());
        return "redirect:/products";
    }
}
