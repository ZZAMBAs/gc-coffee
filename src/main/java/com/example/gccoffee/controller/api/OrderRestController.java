package com.example.gccoffee.controller.api;

import com.example.gccoffee.controller.dto.FoundOrderResponse;
import com.example.gccoffee.controller.dto.OrderRequest;
import com.example.gccoffee.exception.EntityNotFoundException;
import com.example.gccoffee.model.Email;
import com.example.gccoffee.model.Order;
import com.example.gccoffee.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@Slf4j
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/api/v1/orders")
    public Order createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(
                new Email(orderRequest.email()),
                orderRequest.address(),
                orderRequest.postcode(),
                orderRequest.orderItems()
        );
    }
    @GetMapping("/api/v1/orders")
    public List<FoundOrderResponse> findOrders(@RequestParam Optional<UUID> orderId) {
        List<Order> orders;
        if(orderId.isPresent()) {
            orders = List.of(orderService.findOrderById(orderId.get())
                    .orElseThrow(() -> new EntityNotFoundException(MessageFormat.format("{0}에 해당하는 주문이 없습니다", orderId))));
        } else {
            orders = orderService.findAll();
        }

        return orders.stream().map(FoundOrderResponse::convertOrderToFoundOrderResponse).toList();
    }

    @PatchMapping("/api/v1/orders/{orderId}")
    public Order updateOrder(@PathVariable UUID orderId, @RequestBody OrderRequest orderRequest) {

        return orderService.update(orderId,
                orderRequest.address(),
                orderRequest.postcode(),
                orderRequest.orderStatus());
    }

    @DeleteMapping("/api/v1/orders/{orderId}")
    public void deleteOrderById(@PathVariable UUID orderId) {
        orderService.deleteOrderById(orderId);
    }

    @DeleteMapping("api/v1/orders")
    public void deleteAll() {
        orderService.deleteAll();
    }
}
