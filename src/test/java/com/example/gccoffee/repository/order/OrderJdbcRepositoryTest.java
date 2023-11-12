package com.example.gccoffee.repository.order;

import com.example.gccoffee.model.*;
import com.example.gccoffee.repository.product.ProductJdbcRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderJdbcRepositoryTest {
    @Autowired
    private OrderJdbcRepository orderJdbcRepository;
    @Autowired
    private ProductJdbcRepository productJdbcRepository;

    private Product initProduct;
    private Order initOrder;
    @BeforeEach
    void setUp() {
        initProduct = new Product(UUID.randomUUID(), "tempP", Category.COFFEE_BEAN_PACKAGE, 10);
        initOrder = new Order(UUID.randomUUID(),
                new Email("temp@gmail.com"),
                "address",
                "postcode",
                new ArrayList<>(),
                OrderStatus.ACCEPTED,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        productJdbcRepository.insert(initProduct);
        orderJdbcRepository.insert(initOrder);
    }

    @AfterEach
    void cleanUp() {
        orderJdbcRepository.deleteAll();
        productJdbcRepository.deleteAll();
    }

    @Test
    @DisplayName("DB에 주문을 추가할 수 있다.")
    void testInsertOrderToDB() {
        OrderItem orderItem = new OrderItem(initProduct.getProductId(), Category.COFFEE_TRASH, 10000, 2);

        Order order = new Order(UUID.randomUUID(),
                new Email("temp@gmail.com"),
                "address",
                "14552",
                new ArrayList<>(){{
                    add(orderItem);
                }},
                OrderStatus.ACCEPTED,
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS),
                LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        orderJdbcRepository.insert(order);
        Optional<Order> foundOrder = orderJdbcRepository.findById(order.getOrderId());

        assertThat(foundOrder.isPresent(), is(true));
        assertThat(foundOrder.get().getOrderId(), is(order.getOrderId()));
        assertThat(foundOrder.get().getOrderItems(), hasSize(1));
    }

    @Test
    @DisplayName("DB에 이미 존재하는 주문을 업데이트할 수 있다.")
    void testUpdateOrderInDB() {
        initOrder.changeInfo("updateAddress", "21002", OrderStatus.SETTLED);
        orderJdbcRepository.update(initOrder);

        Optional<Order> foundOrder = orderJdbcRepository.findById(initOrder.getOrderId());

        assertThat(foundOrder.isPresent(), is(true));
        assertThat(foundOrder.get().getOrderStatus(), is(OrderStatus.SETTLED));
        assertThat(foundOrder.get().getAddress(), is("updateAddress"));
    }

    @Test
    @DisplayName("주문 Id 값으로 주문을 조회할 수 있다.")
    void testFindOrderById() {
        Optional<Order> foundOrder = orderJdbcRepository.findById(initOrder.getOrderId());

        assertThat(foundOrder.isPresent(), is(true));
        assertThat(foundOrder.get().getOrderId(), is(initOrder.getOrderId()));
        assertThat(foundOrder.get().getEmail().getAddress(), is("temp@gmail.com"));
    }

    @Test
    @DisplayName("전체 주문을 불러올 수 있다.")
    void findAll() {
        List<Order> orders = orderJdbcRepository.findAll();

        assertThat(orders, hasSize(1));
    }

    @Test
    @DisplayName("주문 Id로 특정 주문을 삭제할 수 있다.")
    void deleteById() {
        orderJdbcRepository.deleteById(UUID.randomUUID());

        assertThat(orderJdbcRepository.findAll(), hasSize(1));

        orderJdbcRepository.deleteById(initOrder.getOrderId());

        assertThat(orderJdbcRepository.findAll(), hasSize(0));
    }

    @Test
    @DisplayName("전체 주문을 삭제할 수 있다.")
    void deleteAll() {
        orderJdbcRepository.deleteAll();

        assertThat(orderJdbcRepository.findAll(), hasSize(0));
    }
}
