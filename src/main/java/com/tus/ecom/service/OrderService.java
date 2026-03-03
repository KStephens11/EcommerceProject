package com.tus.ecom.service;

import com.tus.ecom.dto.CategorySalesDto;
import com.tus.ecom.dto.order.OrderItemDto;
import com.tus.ecom.dto.order.OrderItemResponseDto;
import com.tus.ecom.dto.order.OrderResponseDto;
import com.tus.ecom.model.OrderEntity;
import com.tus.ecom.model.OrderItemEntity;
import com.tus.ecom.model.ProductEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.OrderRepository;
import com.tus.ecom.repository.ProductRepository;
import com.tus.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {

        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(String username, List<OrderItemDto> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDto dto : items) {

            ProductEntity product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Product not found: " + dto.getProductId()));

            if (product.getQuantity() < dto.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: " + product.getName());
            }

            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setPrice(product.getPrice());

            order.getItems().add(item);

            total = total.add(
                    product.getPrice()
                            .multiply(BigDecimal.valueOf(dto.getQuantity()))
            );

            product.setQuantity(product.getQuantity() - dto.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);

        OrderEntity savedOrder = orderRepository.save(order);

        return toResponseDto(savedOrder);
    }

    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    private OrderResponseDto toResponseDto(OrderEntity order) {

        OrderResponseDto dto = new OrderResponseDto();

        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setUsername(
                order.getUser() != null ?
                        order.getUser().getUsername() :
                        "Unknown"
        );

        List<OrderItemResponseDto> items = new ArrayList<>();

        if (order.getItems() != null) {
            for (OrderItemEntity item : order.getItems()) {

                OrderItemResponseDto itemDto = getOrderItemResponseDto(item);

                items.add(itemDto);
            }
        }

        dto.setItems(items);

        return dto;
    }

    private static OrderItemResponseDto getOrderItemResponseDto(OrderItemEntity item) {
        OrderItemResponseDto itemDto = new OrderItemResponseDto();

        itemDto.setId(item.getId());
        itemDto.setProductId(
                item.getProduct() != null ?
                        item.getProduct().getId() :
                        null
        );

        itemDto.setProductName(
                item.getProduct() != null ?
                        item.getProduct().getName() :
                        "Deleted product"
        );

        itemDto.setQuantity(item.getQuantity());
        itemDto.setPrice(item.getPrice());
        return itemDto;
    }

    public List<CategorySalesDto> getSalesByCategory() {

        return orderRepository.findSalesByCategory().stream()
                .map(row -> new CategorySalesDto(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }
}
