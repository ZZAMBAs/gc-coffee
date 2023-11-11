package com.example.gccoffee.controller.dto;

import com.example.gccoffee.model.Category;

import java.util.UUID;

public record ProductRequest(UUID productId,
                             String productName,
                             Category category,
                             long price,
                             String description) {
}
