package me.tuhin47.orderservice.service;

import me.tuhin47.client.PaymentService;
import me.tuhin47.client.ProductService;
import me.tuhin47.client.UserService;
import me.tuhin47.config.exception.apierror.EntityNotFoundException;
import me.tuhin47.config.utils.ComputableFutureRequestHandler;
import me.tuhin47.core.enums.PaymentMode;
import me.tuhin47.orderservice.dummy.TopOrderDtoDummy;
import me.tuhin47.orderservice.model.Order;
import me.tuhin47.orderservice.payload.mapper.OrderMapper;
import me.tuhin47.orderservice.payload.request.OrderRequest;
import me.tuhin47.orderservice.payload.response.OrderResponseWithDetails;
import me.tuhin47.orderservice.repository.OrderRepository;
import me.tuhin47.orderservice.service.impl.OrderServiceImpl;
import me.tuhin47.payload.response.PaymentResponse;
import me.tuhin47.payload.response.ProductResponse;
import me.tuhin47.payload.response.TopOrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderServiceImplTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private ProductService productService;
    private PaymentService paymentService;
    private UserService userService;
    private ComputableFutureRequestHandler computableFutureRequestHandler;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderRepository.class);
        productService = mock(ProductService.class);
        paymentService = mock(PaymentService.class);
        userService = mock(UserService.class);
        computableFutureRequestHandler = mock(ComputableFutureRequestHandler.class);
        var orderMapper = Mappers.getMapper(OrderMapper.class);
        orderService = new OrderServiceImpl(orderMapper, orderRepository, productService, paymentService, userService, computableFutureRequestHandler);
    }

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success() {
        //Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));

        when(productService.getProductById(order.getProductId())).thenReturn(getMockProductResponse());

        when(paymentService.getPaymentDetailsByOrderId(order.getId())).thenReturn(getMockPaymentResponse());

        //Actual
        OrderResponseWithDetails orderResponse = orderService.getOrderDetails("1");

        //Verification
        verify(orderRepository, times(1)).findById(anyString());
        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.id());
    }

    @DisplayName("Get Orders - Failure Scenario")
    @Test
    void test_When_Get_Order_NOT_FOUND_then_Not_Found() {

        when(orderRepository.findById(anyString())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> orderService.getOrderDetails("1"));
        assertEquals("Order was not found for parameters {id=1}", exception.getMessage());

        verify(orderRepository, times(1)).findById(anyString());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        String productId = orderService.placeOrderRequest(orderRequest).getProductId();

        verify(orderRepository, times(1)).save(any());

        assertEquals(order.getProductId(), productId);
    }

    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_when_Place_Order_Payment_Fails_then_Order_Placed() {
        //       TODO
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                           .productId("1")
                           .quantity(10)
                           .paymentMode(PaymentMode.CASH)
                           .totalAmount(100)
                           .build();
    }

    private ResponseEntity<PaymentResponse> getMockPaymentResponse() {
        return ResponseEntity.status(HttpStatus.OK).body(PaymentResponse.builder()
                                                                        .paymentDate(Instant.now())
                                                                        .paymentMode(PaymentMode.CASH)
                                                                        .amount(200)
                                                                        .orderId("1")
                                                                        .paymentStatus("ACCEPTED")
                                                                        .build());
    }

    private ResponseEntity<ProductResponse> getMockProductResponse() {
        ProductResponse iPhone = ProductResponse.builder()
                                                .id("2")
                                                .productName("iPhone")
                                                .price(100)
                                                .quantity(200)
                                                .build();
        return ResponseEntity.status(HttpStatus.OK).body(iPhone);
    }

    public static Order getMockOrder() {
        return Order.builder()
                    .orderStatus("PLACED")
                    .orderDate(Instant.now())
                    .id("1")
                    .amount(100)
                    .quantity(200)
                    .productId("1")
                    .build();
    }

    @Test
    void getOrderDetails() {
        // TODO :
    }

    @Test
    void placeOrderRequest() {
        // TODO :
    }

    @Test
    void getTop10OrderByDateRange() {

        var topOrderDtos = Arrays.asList(
            TopOrderDtoDummy.getFirstTopOrderDto(),
            TopOrderDtoDummy.getSecondTopOrderDto()
        );
        // Given
        when(orderRepository.getTopOrderByDateRange(any(Instant.class), any(Instant.class), eq("APPROVED"), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(topOrderDtos));
        when(userService.getAllUsers(any(String[].class)))
            .thenReturn(new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK));


        // When
        List<TopOrderDto> result = orderService.getTop10OrderByDateRange(Instant.now(), Instant.now().plusSeconds(60));

        // Then
        assertEquals(2, result.size());
        assertEquals("buyer1", result.get(0).getBuyerId());
        assertEquals("buyer2", result.get(1).getBuyerId());
    }
}
