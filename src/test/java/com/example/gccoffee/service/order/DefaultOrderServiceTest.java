package com.example.gccoffee.service.order;

import com.example.gccoffee.model.*;
import com.example.gccoffee.repository.order.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultOrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private DefaultOrderService orderService;

    @Test
    @DisplayName("주문 추가를 실행할 수 있다.")
    void testCreateOrder() {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(UUID.randomUUID(), Category.COFFEE_TRASH, 2000L, 3));
        orderItems.add(new OrderItem(UUID.randomUUID(), Category.COFFEE_BEAN_PACKAGE, 4000L, 5));

        when(orderRepository.insert(any()))
                .thenReturn(new Order(UUID.randomUUID(),
                        new Email("email@gmail.com"),
                        "address",
                        "10000",
                        orderItems,
                        OrderStatus.ACCEPTED,
                        LocalDateTime.now(),
                        LocalDateTime.now()));

        Order order = orderService.createOrder(new Email("email@gmail.com"), "address", "10000", orderItems);

        assertThat(order.getOrderItems()).hasSize(2);
        assertThat(order.getAddress()).isEqualTo("address");

        verify(orderRepository).insert(any());
    }

    @Test
    @DisplayName("이미 존재하는 주문을 업데이트할 수 있다.")
    void testUpdateOrder() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.update(any())).thenReturn(new Order(orderId,
                new Email("email@gmail.com"),
                "updatedAddress",
                "10000",
                new ArrayList<>(),
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                LocalDateTime.now()));

        Order updatedOrder = orderService.update(orderId, "updatedAddress", "20220", OrderStatus.CANCELED);

        assertThat(updatedOrder.getOrderId()).isEqualTo(orderId);
        assertThat(updatedOrder.getAddress()).isEqualTo("updatedAddress");

        verify(orderRepository).update(any());
    }

    @Test
    @DisplayName("모든 주문을 조회할 수 있다.")
    void testFindAllOrders() {
        when(orderRepository.findAll()).thenReturn(new ArrayList<>() {{
                    add(new Order(UUID.randomUUID(),
                            new Email("email@gmail.com"),
                            "address",
                            "10000",
                            new ArrayList<>(),
                            OrderStatus.ACCEPTED,
                            LocalDateTime.now(),
                            LocalDateTime.now()));
                    add(new Order(UUID.randomUUID(),
                            new Email("email2@gmail.com"),
                            "address2",
                            "10002",
                            new ArrayList<>(),
                            OrderStatus.ACCEPTED,
                            LocalDateTime.now(),
                            LocalDateTime.now()));
                }});

        List<Order> orders = orderService.findAll();

        assertThat(orders).hasSize(2);

        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("id로 특정 주문을 조회할 수 있다.")
    void testFindOrderById() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(new Order(orderId,
                new Email("email@gmail.com"),
                "address",
                "10000",
                new ArrayList<>(),
                OrderStatus.ACCEPTED,
                LocalDateTime.now(),
                LocalDateTime.now())));

        Optional<Order> foundOrder = orderService.findOrderById(orderId);

        assertThat(foundOrder.isPresent()).isTrue();
        assertThat(foundOrder.get().getOrderId()).isEqualTo(orderId);

        verify(orderRepository,times(1)).findById(orderId);
    }

    @Test
    @DisplayName("id로 특정 주문을 삭제할 수 있다.")
    void testDeleteOrderById() {
        orderService.deleteOrderById(UUID.randomUUID());

        verify(orderRepository).deleteById(any());
    }

    @Test
    @DisplayName("모든 주문을 삭제할 수 있다.")
    void testDeleteAllOrders() {
        orderService.deleteAll();

        verify(orderRepository).deleteAll();
    }
}
