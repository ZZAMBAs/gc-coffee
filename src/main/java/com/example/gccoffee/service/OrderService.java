package com.example.gccoffee.service;

import com.example.gccoffee.model.Email;
import com.example.gccoffee.model.Order;
import com.example.gccoffee.model.OrderItem;
import com.example.gccoffee.model.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    Order createOrder(Email email, String address, String postcode, List<OrderItem> orderItems);
    Order update(UUID orderId, String address, String postcode, OrderStatus orderStatus);
    List<Order> findAll();
    Optional<Order> findOrderById(UUID orderId);
    void deleteOrderById(UUID orderId);
    void deleteAll();
}
