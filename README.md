# 🏦 Banking Application Microservice

A modular, secure, and scalable banking system built using Spring Boot microservices. It features service discovery, API gateway routing, JWT-based authentication, and clean separation of concerns across services.

---

## 📸 Architecture Overview

<img width="1241" height="502" alt="Bank Microservice Architecture" src="https://github.com/user-attachments/assets/ccba6108-0bdf-489d-82a6-1d8740920f41" />

This architecture includes:
- **User Service**
- **Account Service**
- **Notification Service**
- **Eureka Server** (Service Discovery)
- **API Gateway** (Routing & Security)

---

## 🔧 Microservices Breakdown

### 👤 User Service
Handles user management and authentication.

#### 🗂️ Entities:
- `User`: Stores customer credentials and profile data.
- `Employee`: Represents internal bank staff with elevated access.
- `UserAccessLogs`: Tracks login attempts, IP addresses, and session metadata for auditing.

#### 🔐 Features:
- JWT token generation and validation
- Role-based access control
- Audit logging for security compliance

---

### 💰 Account Service
Manages financial data and operations.

#### 🗂️ Entities:
- `Account`: Contains account number, type, balance, and status.
- `Transaction`: Records deposits, withdrawals, and transfers with timestamps.
- `Ledger`: Maintains a double-entry record of all financial movements for reconciliation.

#### 🔐 Features:
- Secure transaction processing
- Balance validation and overdraft protection
- Integration with Notification Service for alerts

---

### 📢 Notification Service
Handles outbound communication to users.

#### 🗂️ Entity:
- `Notification`: Stores message content, delivery status, and recipient metadata.

#### 🔐 Features:
- Email and SMS delivery
- Event-driven triggers from other services
- Retry and failure logging

---

## 🚪 API Gateway
- Centralized routing for all client requests
- Validates JWT tokens before forwarding
- Handles CORS, rate limiting, and logging

---

## 🧭 Eureka Server
- Dynamic service registration and discovery
- Enables load balancing and fault tolerance

---

## 🔐 Authentication
- Stateless JWT-based authentication
- Tokens include user roles and expiration
- Secured endpoints across all services

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- Docker (optional)

### Run Locally
```bash
# Start Eureka Server
cd eureka-server
mvn spring-boot:run

# Start API Gateway
cd api-gateway
mvn spring-boot:run

# Start Microservices
cd user-service
mvn spring-boot:run

cd account-service
mvn spring-boot:run

cd notification-service
mvn spring-boot:run
