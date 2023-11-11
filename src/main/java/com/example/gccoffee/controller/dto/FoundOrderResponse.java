package com.example.gccoffee.controller.dto;

import com.example.gccoffee.model.Order;
import com.example.gccoffee.model.OrderItem;

import java.util.List;
import java.util.UUID;

public record FoundOrderResponse (UUID orderId,
                                  String email,
                                  String address,
                                  String status,
                                  List<OrderItem> orderItems) {

    public static FoundOrderResponse convertOrderToFoundOrderResponse(Order order) {
        return new FoundOrderResponse(order.getOrderId(),
                order.getEmail().getAddress(),
                order.getAddress(),
                order.getOrderStatus().toString(),
                order.getOrderItems());
    }
}
