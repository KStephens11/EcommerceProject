package com.tus.ecom.unit_tests;

import com.tus.ecom.dto.SalesDto;
import com.tus.ecom.dto.order.OrderItemDto;
import com.tus.ecom.dto.order.OrderResponseDto;
import com.tus.ecom.model.*;
import com.tus.ecom.repository.OrderRepository;
import com.tus.ecom.repository.ProductRepository;
import com.tus.ecom.repository.UserRepository;
import com.tus.ecom.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    OrderRepository orderRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        userRepository = Mockito.mock(UserRepository.class);

        orderService = new OrderService(orderRepository, productRepository, userRepository);
    }

    private OrderItemDto createOrderItem(Integer quantity) {
        OrderItemDto dto = new OrderItemDto();
        dto.setProductId(1L);
        dto.setQuantity(quantity);
        return dto;
    }

    @Test
    void createOrderTestValid() {

        String username = "Joe";

        UserEntity user = new UserEntity();
        user.setUsername(username);

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(100));
        product.setQuantity(10);

        OrderItemDto itemDto = createOrderItem(2);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<OrderItemDto> items = List.of(itemDto);

        orderService.createOrder(username, items);

        ArgumentCaptor<ProductEntity> productCaptor =
                ArgumentCaptor.forClass(ProductEntity.class);

        verify(productRepository).save(productCaptor.capture());

        ProductEntity updatedProduct = productCaptor.getValue();
        assertEquals(8, updatedProduct.getQuantity());

        ArgumentCaptor<OrderEntity> orderCaptor =
                ArgumentCaptor.forClass(OrderEntity.class);

        verify(orderRepository).save(orderCaptor.capture());

        OrderEntity savedOrder = orderCaptor.getValue();

        assertEquals(user, savedOrder.getUser());
        assertEquals(200, savedOrder.getTotalAmount().intValue());
        assertEquals(1, savedOrder.getItems().size());
    }

    @Test
    void createOrderTestEmptyItems() {

        List<OrderItemDto> emptyItems = new ArrayList<>();

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder("Joe", emptyItems));

        assertEquals("Order must contain at least one item", e.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrderTestUserNotFound() {

        when(userRepository.findByUsername("Joe"))
                .thenReturn(Optional.empty());

        List<OrderItemDto> items = List.of(createOrderItem(1));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder("Joe", items));

        assertEquals("User not found", e.getMessage());
    }

    @Test
    void createOrderTestProductNotFound() {

        when(userRepository.findByUsername("Joe"))
                .thenReturn(Optional.of(new UserEntity()));

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        List<OrderItemDto> items = List.of(createOrderItem(1));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder("Joe", items));

        assertEquals("Product not found: 1", e.getMessage());
    }

    @Test
    void createOrderTestInsufficientStock() {

        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setName("Phone");
        product.setQuantity(1);
        product.setPrice(BigDecimal.TEN);

        when(userRepository.findByUsername("Joe"))
                .thenReturn(Optional.of(new UserEntity()));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        List<OrderItemDto> items = List.of(createOrderItem(5));

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder("Joe", items));

        assertEquals("Insufficient stock for product: Phone", e.getMessage());
    }

    @Test
    void getAllOrdersTest() {

        OrderEntity order = new OrderEntity();
        order.setId(1L);
        order.setUser(new UserEntity());
        order.getUser().setUsername("Joe");
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(BigDecimal.valueOf(150));

        when(orderRepository.findAll())
                .thenReturn(List.of(order));

        List<OrderResponseDto> responses = orderService.getAllOrders();

        assertEquals(1, responses.size());
        assertEquals("Joe", responses.getFirst().getUsername());
        assertEquals(150, responses.getFirst().getTotalAmount().intValue());
    }

    @Test
    void getSalesByCategoryTest() {

        Object[] row1 = new Object[]{"Electronics", BigDecimal.valueOf(500)};
        Object[] row2 = new Object[]{"Books", BigDecimal.valueOf(200)};

        when(orderRepository.findSalesByCategory())
                .thenReturn(List.of(row1, row2));

        List<SalesDto> result = orderService.getSalesByCategory();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getSaleType());
        assertEquals(500, result.get(0).getTotal().intValue());
        assertEquals("Books", result.get(1).getSaleType());
        assertEquals(200, result.get(1).getTotal().intValue());
    }

    @Test
    void getSalesByBrandTest() {

        Object[] row1 = new Object[]{"Apple", BigDecimal.valueOf(800)};
        Object[] row2 = new Object[]{"Samsung", BigDecimal.valueOf(350)};

        when(orderRepository.findSalesByBrand())
                .thenReturn(List.of(row1, row2));

        List<SalesDto> result = orderService.getSalesByBrand();

        assertEquals(2, result.size());
        assertEquals("Apple", result.get(0).getSaleType());
        assertEquals(800, result.get(0).getTotal().intValue());
        assertEquals("Samsung", result.get(1).getSaleType());
        assertEquals(350, result.get(1).getTotal().intValue());
    }

}