package com.example.gccoffee.controller.dto;

import com.example.gccoffee.model.Category;

public record ProductRequest(String productName,
                             Category category,
                             long price,
                             String description) {
}
