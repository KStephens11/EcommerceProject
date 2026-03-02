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

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderEntity createOrder(UserEntity user, List<OrderItemDto> itemDtos) {
        if (itemDtos == null || itemDtos.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemDto dto : itemDtos) {
            ProductEntity product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + dto.getProductId()));

            if (product.getQuantity() < dto.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            OrderItemEntity item = new OrderItemEntity();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setPrice(product.getPrice());

            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));

            // Update stock
            product.setQuantity(product.getQuantity() - dto.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
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
        dto.setUsername(order.getUser() != null ? order.getUser().getUsername() : "Unknown");


        // Populate items
        List<OrderItemResponseDto> items = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItemEntity item : order.getItems()) {
                OrderItemResponseDto itemDto = new OrderItemResponseDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
                itemDto.setProductName(item.getProduct() != null ? item.getProduct().getName() : "Deleted product");
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                items.add(itemDto);
            }
        }
        dto.setItems(items);

        return dto;
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
