# Account Recipients Service – v1.0

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jcondotta_bank-account-recipients&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jcondotta_bank-account-recipients)  
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=jcondotta_bank-account-recipients&metric=coverage)](https://sonarcloud.io/summary/new_code?id=jcondotta_bank-account-recipients)

This project is part of a **microservice-based architecture** responsible for managing **bank account recipients**.  
It provides RESTful APIs to **create, retrieve, and delete recipients**, while publishing **domain events** to downstream systems in a reliable and idempotent way.

The service is designed with **scalability, resilience, and maintainability** in mind, following **DDD, Clean Architecture, and event-driven principles**.

---

## 🧱 Architecture Overview

- Multi-module Maven project
- Clear separation of concerns
  - Domain
  - Application
  - Infrastructure / Service
- Event-driven architecture
- Idempotent command & event processing
- Cloud-ready, locally reproducible

---

## 🛠️ Tech Stack

### Languages & Frameworks
- Java 17
- Spring Boot 3.x
- Spring MVC
- Spring Data Redis
- Spring Kafka
- Spring Cloud OpenFeign
- Spring Boot Actuator
- Spring AOP

---

### Architecture & Design
- Domain-Driven Design (DDD)
- Clean Architecture / Hexagonal Architecture
- Domain Events
- Integration Events
- Idempotency control using Redis
- Multi-module Maven structure

---

### Infrastructure
- Amazon DynamoDB (Enhanced Client)
- Apache Kafka
- Redis
- AWS (LocalStack for local development)
- Docker & Docker Compose
- Terraform (Infrastructure as Code)

---

### Observability & Monitoring
- Micrometer
- Prometheus
- Spring Boot Actuator

---

### Mapping & Utilities
- MapStruct (compile-time mapping)
- Commons Codec
- Lombok

---

### Testing
- JUnit 5
- Mockito
- AssertJ
- Testcontainers
  - LocalStack
  - Redis
- WireMock
- Rest-Assured
- LogCaptor

---

## ✨ Features

- Recipient Management
  - Create recipients
  - Fetch recipients
  - Delete recipients
- Domain Events
  - RecipientCreatedEvent
  - RecipientDeletedEvent
- Idempotent Event Publishing
  - Redis-backed idempotency keys
- Event-driven Integration
  - Kafka-based messaging
- Observability
  - Metrics exposed via Prometheus
- Local Cloud Simulation
  - Full AWS stack via LocalStack
- CI-ready
  - Designed for GitHub Actions pipelines

---

## 📦 Project Structure

account-recipients-system
│
├── account-recipients-domain-service
│ └── Domain model, value objects, events
│
├── account-recipients-application-service
│ └── Use cases, mappers, ports
│
├── account-recipients-service
│ └── REST controllers, Kafka publishers, Redis, AWS adapters
│
└── terraform
└── Infrastructure definitions


---

## 🔑 Prerequisites

- Java 17
- Maven
- Docker
- Docker Compose
- Terraform
- LocalStack
- tflocal

---

## 🚀 Getting Started

### Clone the repository
```bash
git clone https://github.com/jcondotta/bank-account-recipients.git
cd bank-account-recipients
```
