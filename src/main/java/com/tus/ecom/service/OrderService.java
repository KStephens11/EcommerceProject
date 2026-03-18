package com.tus.ecom.service;

import com.tus.ecom.dto.SalesDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    // Create Order
    @Transactional
    public OrderResponseDto createOrder(String username, List<OrderItemDto> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Create Order Entity and set fields
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        // Create Entities from DTOs in
        for (OrderItemDto dto : items) {

            if (dto.getProductId() == null) {
                throw new IllegalArgumentException("Product ID is required");
            }

            if (dto.getQuantity() == null) {
                throw new IllegalArgumentException("Quantity is required");
            }

            if (dto.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            ProductEntity product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Product not found: " + dto.getProductId()));

            if (product.getQuantity() < dto.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            // Creating OrderItemEntity and adding it to OrderEntity List
            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductCategory(product.getCategory());
            item.setProductBrand(product.getBrand());
            //save ordered quantity
            item.setQuantity(dto.getQuantity());

            item.setPrice(product.getPrice());

            order.getItems().add(item);

            total = total.add(
                    product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity()))
            );

            // Saving Quantity change in products
            product.setQuantity(product.getQuantity() - dto.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);

        return mapOrderToDto(orderRepository.save(order));
    }

    // Get All Orders
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapOrderToDto)
                .toList();
    }

    // Get Sales by Category
    public List<SalesDto> getSalesByCategory() {
        return orderRepository.findSalesByCategory()
                .stream()
                .map(row -> new SalesDto(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }

    // Get Sales by Brand
    public List<SalesDto> getSalesByBrand() {
        return orderRepository.findSalesByBrand()
                .stream()
                .map(row -> new SalesDto(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }

    // Mappers
    private OrderResponseDto mapOrderToDto(OrderEntity order) {

        // Create OrderRepsonseDto and set fields
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setUsername(order.getUser() != null ? order.getUser().getUsername() : "Unknown");

        // Create OrderItemResponseDtos and set to OrderResponseDto
        dto.setItems(
                order.getItems() == null ?
                        List.of() :
                        order.getItems().stream()
                                .map(this::mapOrderItemToDto)
                                .toList()
        );

        return dto;
    }

    private OrderItemResponseDto mapOrderItemToDto(OrderItemEntity item) {

        OrderItemResponseDto dto = new OrderItemResponseDto();

        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setProductCategory(item.getProductCategory());
        dto.setProductBrand(item.getProductBrand());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());

        return dto;
    }
}