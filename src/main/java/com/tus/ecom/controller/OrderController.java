package com.tus.ecom.controller;

import com.tus.ecom.dto.CategorySalesDto;
import com.tus.ecom.dto.order.OrderRequest;
import com.tus.ecom.dto.order.OrderResponseDto;
import com.tus.ecom.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody OrderRequest req,
            Authentication authentication) {

        OrderResponseDto response = orderService.createOrder(
                authentication.getName(),
                req.getItems()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/stats/sales-by-category")
    public ResponseEntity<List<CategorySalesDto>> getSalesByCategory() {
        return ResponseEntity.ok(orderService.getSalesByCategory());
    }

    @GetMapping("/stats/sales-by-brand")
    public ResponseEntity<List<CategorySalesDto>> getSalesByBrand() {
        return ResponseEntity.ok(orderService.getSalesByBrand());
    }

}