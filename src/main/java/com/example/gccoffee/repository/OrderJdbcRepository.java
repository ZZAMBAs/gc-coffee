package com.example.gccoffee.repository;

import com.example.gccoffee.model.Order;
import com.example.gccoffee.model.OrderItem;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class OrderJdbcRepository implements OrderRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OrderJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Order insert(Order order) {
        jdbcTemplate.update("INSERT INTO orders " +
                        "VALUES (UUID_TO_BIN(:orderId), :email, :address, :postcode, :orderStatus, :createdAt, :updatedAt)",
                toOrderParamMap(order));
        order.getOrderItems().forEach(item -> jdbcTemplate.update("INSERT INTO order_items (order_id, product_id, category, price, quantity, created_at, updated_at) " +
                        "VALUES (UUID_TO_BIN(:orderId), :productId, :category, :price, :quantity, :createdAt, :updatedAt)",
                toOrderItemParamMap(order.getOrderId(), order.getCreatedAt(), order.getUpdatedAt(), item)));

        return order;
    }

    private Map<String, Object> toOrderParamMap(Order order) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("orderId", order.getOrderId().toString().getBytes());
        hashMap.put("email", order.getEmail().getAddress());
        hashMap.put("address", order.getAddress());
        hashMap.put("postcode", order.getPostcode());
        hashMap.put("orderStatus", order.getOrderStatus().toString());
        hashMap.put("createdAt", order.getCreatedAt());
        hashMap.put("updatedAt", order.getUpdatedAt());

        return hashMap;
    }

    private Map<String, Object> toOrderItemParamMap(UUID orderId, LocalDateTime createdAt, LocalDateTime updatedAt, OrderItem orderItem) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("orderId", orderId.toString().getBytes());
        hashMap.put("productId", orderItem.productId());
        hashMap.put("category", orderItem.category());
        hashMap.put("price", orderItem.price());
        hashMap.put("quantity", orderItem.quantity());
        hashMap.put("createdAt", createdAt);
        hashMap.put("updatedAt", updatedAt);

        return hashMap;
    }
}
