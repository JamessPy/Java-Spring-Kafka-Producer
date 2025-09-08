# Order-Service

## Overview
Order-Service provides a REST API for creating and retrieving orders.  
It also implements an **event-driven architecture** by recording domain events in an Outbox table.  
These events are later published to Kafka topics (e.g., `order.created`) and consumed by **Billing-Service**.

### Architecture
- **Synchronous (REST):** Create and list orders.
- **Asynchronous (Events):** On order creation, an **Outbox** entry is stored in the DB.  
  A background **Outbox Relay** publishes the events to Kafka.
- **Consumers:** Billing-Service consumes `order.created` and produces payment events.
- **Reliability:** Idempotent consumers, retry strategy, and dead-letter topics (DLT).

### Tech Stack
- Spring Boot (Web, JPA, Kafka)
- Springdoc OpenAPI (Swagger UI)

### Runtime
- **Port:** `8080`  
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## REST API (OpenAPI Spec)
[OpenAPI](docs/openapi-order.pretty.json)