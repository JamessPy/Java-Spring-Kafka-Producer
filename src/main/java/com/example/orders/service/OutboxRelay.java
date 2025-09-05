package com.example.orders.service;

import com.example.orders.domain.OutboxEvent;
import com.example.orders.repository.OutboxRepository;

import jakarta.transaction.Transactional;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OutboxRelay {

    private final OutboxRepository outboxRepo;
    private final KafkaTemplate<String, String> kafka;
    private final String topic;

    public OutboxRelay(OutboxRepository outboxRepo,
                       KafkaTemplate<String, String> kafka,
                       @Value("${order.topic.name}") String topic) {
        this.outboxRepo = outboxRepo;
        this.kafka = kafka;
        this.topic = topic;
    }

    @Transactional
    @Scheduled(fixedDelay = 15000)  // Check every 3 seconds
    public void flush() {
        List<OutboxEvent> events = outboxRepo.findByProcessedFalse();
        if (events.isEmpty()) return;

        for (OutboxEvent e : events) {
            try {
                //
                String key = null; 

                // Sencronize with Kafka
                RecordMetadata meta = kafka.send(topic, key, e.getPayload())
                        .get(10, TimeUnit.SECONDS)
                        .getRecordMetadata();

                // Mark if successful
                e.setProcessed(true);
                outboxRepo.save(e);

                System.out.printf("Outbox[%d] -> Kafka topic=%s partition=%d offset=%d @ %s%n",
                        e.getId(), meta.topic(), meta.partition(), meta.offset(), Instant.now());
            } catch (Exception ex) {
                // Log and retry later
                System.err.printf("Outbox[%d] publish FAILED: %s%n", e.getId(), ex.getMessage());
            }
        }
    }
}
