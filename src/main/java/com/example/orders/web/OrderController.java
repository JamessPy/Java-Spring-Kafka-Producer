package com.example.orders.web;

import com.example.orders.service.OrderService;
import com.example.orders.web.dto.OrderDtos;
import com.example.orders.web.dto.OrderDtos.OrderResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody OrderDtos.OrderCreateRequest req) {
        OrderResponse res = service.createOrder(req);  // same signature as in service
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    
    @GetMapping
    public List<OrderResponse> getAll() {
        return service.getAllOrders();
    }
    @GetMapping("/outbox")
    public List<OrderDtos.OutboxResponse> getOutboxEvents() {
        return service.getOutboxEvents();
    }
}
