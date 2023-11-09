package com.example.gccoffee.controller;

import com.example.gccoffee.model.OrderItem;

import java.util.List;

// DTO에서는 다른 타입으로의 변환과 관련된 로직을 넣을 수 있다.
public record CreateOrderRequest(
        String email,
        String address,
        String postcode,
        List<OrderItem> orderItems
) {
}
