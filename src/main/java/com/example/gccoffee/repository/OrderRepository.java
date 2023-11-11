package com.example.gccoffee.repository;

import com.example.gccoffee.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order insert(Order order);
    Order update(Order order);
    Optional<Order> findById(UUID orderId);
    List<Order> findAll();
    void deleteById(UUID orderId);
    void deleteAll();
}
