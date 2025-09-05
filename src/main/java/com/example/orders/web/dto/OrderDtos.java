package com.example.orders.web.dto;

public final class OrderDtos {
    private OrderDtos() {}

    public static record OrderCreateRequest(String product, Integer quantity) {}
    public static record OrderResponse(Long id, String product, Integer quantity) {}
    public record OutboxResponse(
            Long id,
            String aggregateType,
            String eventType,
            String payload,
            boolean processed
    ) {}
}