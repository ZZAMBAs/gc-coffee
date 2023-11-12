package com.example.gccoffee.controller.api;

import com.example.gccoffee.config.AbstractRestDocsTest;
import com.example.gccoffee.controller.dto.OrderRequest;
import com.example.gccoffee.model.*;
import com.example.gccoffee.service.order.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = OrderRestController.class)
class OrderRestControllerTest extends AbstractRestDocsTest {
    @MockBean
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 생성 요청을 보낼 수 있다.")
    void testCreateOrder() {
        ArrayList<OrderItem> orderItems = new ArrayList<>(){{
            add(new OrderItem(UUID.randomUUID(), Category.COFFEE_BEAN_PACKAGE, 10000L, 3));
            add(new OrderItem(UUID.randomUUID(), Category.COFFEE_TRASH, 200L, 15));
        }};
        Email email = new Email("email@gmail.com");
        UUID orderId = UUID.randomUUID();

        when(orderService.createOrder(email, "address", "12345", orderItems))
                .thenReturn(new Order(orderId,
                        email,
                        "address",
                        "12345",
                        orderItems,
                        OrderStatus.ACCEPTED,
                        LocalDateTime.now(),
                        LocalDateTime.now()));

        String jsonRequest = convertJsonStyleString(new OrderRequest("email@gmail.com",
                "address",
                "12345",
                orderItems,
                null));


        try {
            this.mockMvc.perform(post("/api/v1/orders")
                            .content(jsonRequest)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                    .andDo(print())
                    .andDo(document("{class-name}/{method-name}",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("orderId").description("주문 ID"),
                                    fieldWithPath("email.address").description("주문 이메일 주소"),
                                    fieldWithPath("address").description("주문 주소"),
                                    fieldWithPath("postcode").description("주문 우편 번호"),
                                    fieldWithPath("orderItems").description("주문 상품 목록"),
                                    fieldWithPath("orderItems[].productId").description("주문 상품 ID"),
                                    fieldWithPath("orderItems[].category").description("주문 상품 카테고리"),
                                    fieldWithPath("orderItems[].price").description("상품 한 개 가격"),
                                    fieldWithPath("orderItems[].quantity").description("상품 구매 개수"),
                                    fieldWithPath("orderStatus").description("주문 상태"),
                                    fieldWithPath("createdAt").description("주문 생성 일시"),
                                    fieldWithPath("updatedAt").description("주문 업데이트 일시")
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException("mockmvc 처리 중 예외 발생", e);
        }
    }

    @Test
    @DisplayName("모든 주문을 조회할 수 있다.")
    void testFindAllOrders() {
        when(orderService.findAll())
                .thenReturn(new ArrayList<>(){{
                    add(new Order(UUID.randomUUID(),
                            new Email("address@gmail.com"),
                            "address",
                            "12345",
                            new ArrayList<>(){{
                                add(new OrderItem(UUID.randomUUID(), Category.COFFEE_BEAN_PACKAGE, 10000L, 3));
                            }},
                            OrderStatus.ACCEPTED,
                            LocalDateTime.now(),
                            LocalDateTime.now()));
                }});
        try {
            this.mockMvc.perform(get("/api/v1/orders").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andDo(print())
                    .andDo(document("{class-name}/{method-name}",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("[].orderId").description("주문 ID"),
                                    fieldWithPath("[].email").description("주문 이메일"),
                                    fieldWithPath("[].address").description("주문 주소"),
                                    fieldWithPath("[].status").description("주문 상태"),
                                    fieldWithPath("[].orderItems").description("주문 상품 목록"),
                                    fieldWithPath("[].orderItems[].productId").description("주문 상품 ID"),
                                    fieldWithPath("[].orderItems[].category").description("주문 상품 카테고리"),
                                    fieldWithPath("[].orderItems[].price").description("상품 한 개 가격"),
                                    fieldWithPath("[].orderItems[].quantity").description("상품 구매 개수")
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException("mockmvc 처리 중 예외 발생", e);
        }
    }

    @Test
    @DisplayName("id로 특정 주문을 조회할 수 있다.")
    void testFindOrderById() {
        UUID orderId = UUID.randomUUID();

        when(orderService.findOrderById(orderId))
                .thenReturn(Optional.of(new Order(orderId,
                            new Email("address@gmail.com"),
                            "address",
                            "12345",
                            new ArrayList<>(){{
                                add(new OrderItem(UUID.randomUUID(), Category.COFFEE_BEAN_PACKAGE, 10000L, 3));
                            }},
                            OrderStatus.ACCEPTED,
                            LocalDateTime.now(),
                            LocalDateTime.now())));
        try {
            this.mockMvc.perform(get("/api/v1/orders?orderId={id}", orderId).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].orderId").value(orderId.toString()))
                    .andDo(print())
                    .andDo(document("{class-name}/{method-name}",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("[].orderId").description("주문 ID"),
                                    fieldWithPath("[].email").description("주문 이메일"),
                                    fieldWithPath("[].address").description("주문 주소"),
                                    fieldWithPath("[].status").description("주문 상태"),
                                    fieldWithPath("[].orderItems").description("주문 상품 목록"),
                                    fieldWithPath("[].orderItems[].productId").description("주문 상품 ID"),
                                    fieldWithPath("[].orderItems[].category").description("주문 상품 카테고리"),
                                    fieldWithPath("[].orderItems[].price").description("상품 한 개 가격"),
                                    fieldWithPath("[].orderItems[].quantity").description("상품 구매 개수")
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException("mockmvc 처리 중 예외 발생", e);
        }
    }

    @Test
    @DisplayName("주문을 업데이트 할 수 있다.")
    void testUpdateOrder() {
        UUID orderId = UUID.randomUUID();

        when(orderService.update(orderId, "address", "12345", OrderStatus.SHIPPED))
                .thenReturn(new Order(orderId,
                        new Email("address@gmail.com"),
                        "address",
                        "12345",
                        new ArrayList<>(){{
                            add(new OrderItem(UUID.randomUUID(), Category.COFFEE_BEAN_PACKAGE, 10000L, 3));
                        }},
                        OrderStatus.SHIPPED,
                        LocalDateTime.now(),
                        LocalDateTime.now()));

        try {
            this.mockMvc.perform(patch("/api/v1/orders/{id}", orderId)
                            .content(convertJsonStyleString(new OrderRequest(null, "address", "12345", null, OrderStatus.SHIPPED)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                    .andExpect(jsonPath("$.orderStatus").value(OrderStatus.SHIPPED.toString()))
                    .andDo(print())
                    .andDo(document("{class-name}/{method-name}",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("orderId").description("주문 ID"),
                                    fieldWithPath("email.address").description("주문 이메일 주소"),
                                    fieldWithPath("address").description("주문 주소"),
                                    fieldWithPath("postcode").description("주문 우편 번호"),
                                    fieldWithPath("orderItems").description("주문 상품 목록"),
                                    fieldWithPath("orderItems[].productId").description("주문 상품 ID"),
                                    fieldWithPath("orderItems[].category").description("주문 상품 카테고리"),
                                    fieldWithPath("orderItems[].price").description("상품 한 개 가격"),
                                    fieldWithPath("orderItems[].quantity").description("상품 구매 개수"),
                                    fieldWithPath("orderStatus").description("주문 상태"),
                                    fieldWithPath("createdAt").description("주문 생성 일시"),
                                    fieldWithPath("updatedAt").description("주문 업데이트 일시")
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException("mockmvc 처리 중 예외 발생", e);
        }
    }

    @Test
    @DisplayName("주문을 삭제할 수 있다.")
    void testDeleteOrderById() {
        UUID orderId = UUID.randomUUID();

        try {
            this.mockMvc.perform(delete("/api/v1/orders/{id}", orderId))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("{class-name}/{method-name}",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            throw new RuntimeException("mockmvc 처리 중 예외 발생", e);
        }
    }

    @Test
    @DisplayName("모든 주문을 삭제할 수 있다.")
    void testDeleteAllOrders() {
        try {
            this.mockMvc.perform(delete("/api/v1/orders"))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("{class-name}/{method-name}",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint())));
        } catch (Exception e) {
            throw new RuntimeException("mockmvc 처리 중 예외 발생", e);
        }
    }

    private String convertJsonStyleString(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json marshalling error", e);
        }
    }
}
