package com.example.orders.service;

import com.example.orders.domain.Order;
import com.example.orders.domain.OutboxEvent;
import com.example.orders.repository.OrderRepository;
import com.example.orders.repository.OutboxRepository;
import com.example.orders.web.dto.OrderDtos.OrderCreateRequest;
import com.example.orders.web.dto.OrderDtos.OrderResponse;
import com.example.orders.web.dto.OrderDtos.OutboxResponse;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orders;
    private final OutboxRepository outboxRepo;

    public OrderService(OrderRepository orders, OutboxRepository outboxRepo) {
        this.orders = orders;
        this.outboxRepo = outboxRepo; // ✅ constructor injection
    }

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest req) {
        // Order kaydet
        Order entity = new Order(req.product(), req.quantity());
        Order saved = orders.save(entity);

        // Create Outbox Event
        String payload = """
            {
              "id": %d,
              "product": "%s",
              "quantity": %d
            }
            """.formatted(saved.getId(), saved.getProduct(), saved.getQuantity());

        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("Order");
        event.setEventType("OrderCreated");
        event.setPayload(payload);

        outboxRepo.save(event); // Write to outbox

        return new OrderResponse(saved.getId(), saved.getProduct(), saved.getQuantity());
    }
    public List<OrderResponse> getAllOrders() {
        return orders.findAll()
                .stream()
                .map(o -> new OrderResponse(o.getId(), o.getProduct(), o.getQuantity()))
                .toList();
    }
    public List<OutboxResponse> getOutboxEvents() {
        return outboxRepo.findAll()
                .stream()
                .map(e -> new OutboxResponse(
                        e.getId(),
                        e.getAggregateType(),
                        e.getEventType(),
                        e.getPayload(),
                        e.isProcessed()
                ))
                .toList();
    }
}
