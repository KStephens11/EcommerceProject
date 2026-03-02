package com.tus.ecom.controller;

import com.tus.ecom.dto.CategorySalesDto;
import com.tus.ecom.dto.order.OrderItemResponseDto;
import com.tus.ecom.dto.order.OrderRequest;
import com.tus.ecom.dto.order.OrderResponseDto;
import com.tus.ecom.model.OrderEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.UserRepository;
import com.tus.ecom.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto createOrder(@RequestBody OrderRequest req, Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OrderEntity order = orderService.createOrder(user, req.getItems());

        // Map to DTO
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setUsername(user.getUsername());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponseDto> itemDtos = order.getItems().stream().map(item -> {
            OrderItemResponseDto itemDto = new OrderItemResponseDto();
            itemDto.setId(item.getId());
            itemDto.setProductId(item.getProduct().getId());
            itemDto.setProductName(item.getProduct().getName());
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());
            return itemDto;
        }).toList();

        dto.setItems(itemDtos);
        return dto;
    }

    @GetMapping
    public List<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/stats/sales-by-category")
    public List<CategorySalesDto> getSalesByCategory() {
        return orderService.getSalesByCategory();
    }

}
