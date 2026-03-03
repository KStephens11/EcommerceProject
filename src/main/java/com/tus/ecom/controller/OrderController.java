package com.tus.ecom.controller;

import com.tus.ecom.dto.CategorySalesDto;
import com.tus.ecom.dto.order.OrderRequest;
import com.tus.ecom.dto.order.OrderResponseDto;
import com.tus.ecom.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto createOrder(
            @RequestBody OrderRequest req,
            Authentication authentication) {

        return orderService.createOrder(
                authentication.getName(),
                req.getItems()
        );
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