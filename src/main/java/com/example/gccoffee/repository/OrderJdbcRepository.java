package com.example.gccoffee.repository;

import com.example.gccoffee.model.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.gccoffee.JdbcUtils.toUUID;

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
                        "VALUES (UUID_TO_BIN(:orderId), UUID_TO_BIN(:productId), :category, :price, :quantity, :createdAt, :updatedAt)",
                toOrderItemParamMap(order.getOrderId(), order.getCreatedAt(), order.getUpdatedAt(), item)));

        return order;
    }

    @Override
    public Order update(Order order) {
        int updatedCnt = jdbcTemplate.update("UPDATE orders SET address = :address, postcode = :postcode, order_status = :orderStatus, updated_at = :updatedAt " +
                        "WHERE order_id = UUID_TO_BIN(:orderId)",
                toOrderParamMap(order));

        if (updatedCnt != 1) {
            throw new RuntimeException("주문이 제대로 업데이트 되지 않음.");
        }

        return order;
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return jdbcTemplate.query("SELECT o.order_id, o.email, o.address, o.postcode, o.order_status, o.created_at, o.updated_at, " +
                        "oi.product_id, oi.category, oi.price, oi.quantity FROM orders o JOIN order_items oi ON o.order_id = oi.order_id " +
                        "WHERE o.order_id = UUID_TO_BIN(:orderId) " +
                        "ORDER BY o.order_id",
                Collections.singletonMap("orderId", orderId),
                this::orderResultSetExtractor).stream().findFirst();
    }

    @Override
    public List<Order> findAll() {
        return jdbcTemplate.query("SELECT o.order_id, o.email, o.address, o.postcode, o.order_status, o.created_at, o.updated_at, " +
                "oi.product_id, oi.category, oi.price, oi.quantity FROM orders o JOIN order_items oi ON o.order_id = oi.order_id " +
                "ORDER BY o.order_id",
                this::orderResultSetExtractor);
    }

    @Override
    public void deleteById(UUID orderId) {
        jdbcTemplate.update("DELETE FROM orders WHERE order_id = :orderId",
                Collections.singletonMap("orderId", orderId));
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM orders", Collections.emptyMap());
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
        hashMap.put("productId", orderItem.productId().toString().getBytes());
        hashMap.put("category", orderItem.category().toString());
        hashMap.put("price", orderItem.price());
        hashMap.put("quantity", orderItem.quantity());
        hashMap.put("createdAt", createdAt);
        hashMap.put("updatedAt", updatedAt);

        return hashMap;
    }

    private List<Order> orderResultSetExtractor(ResultSet rs) {
        try {
            Map<UUID, Order> orderMap = new HashMap<>();

            while (rs.next()) {
                UUID orderId = toUUID(rs.getBytes("order_id"));
                Email email = new Email(rs.getString("email"));
                String address = rs.getString("address");
                String postcode = rs.getString("postcode");
                OrderStatus orderStatus = OrderStatus.valueOf(rs.getString("order_status"));
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

                if (orderMap.get(orderId) == null) {
                    orderMap.put(orderId, new Order(orderId,
                            email,
                            address,
                            postcode,
                            new ArrayList<>(),
                            orderStatus,
                            createdAt,
                            updatedAt));
                }
                Order order = orderMap.get(orderId);

                UUID productId = toUUID(rs.getBytes("product_id"));
                Category category = Category.valueOf(rs.getString("category"));
                long price = rs.getLong("price");
                int quantity = rs.getInt("quantity");

                OrderItem orderItem = new OrderItem(productId, category, price, quantity);
                order.getOrderItems().add(orderItem);
            }

            return new ArrayList<>(orderMap.values());
        } catch (SQLException e) {
            throw new RuntimeException("ResultSet을 읽는 도중 에러 발생.");
        }
    }
}
