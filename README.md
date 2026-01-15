# Zilch Card Service
## Overview
Zilch Card Service is a simplified demo card service that demonstrates simplified card creation process.  
The service follows event sourcing principles to maintain an immutable audit trail of all card-related operations.
For the purpose of this exercise it intentionally uses bleeding‑edge versions of the platform: the latest Java, Spring Boot and supporting libraries.  

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Requirements](#requirements)
- [Local Run](#local-run)
---

## Tech Stack

**Runtime / Frameworks**

- **Java**: 25 
- **Spring Boot**: 4.0.1
- **PostgreSQL**
- **Kafka**

**Documentation**

- **SpringDoc OpenAPI** 

**Testing**

- **Spock Framework** 
- **Groovy** 5.x 
- **Spring Boot Test** 
- **Testcontainers**:
    - PostgreSQL module for containerized database tests
    - Kafka module for containerized message queue tests
- **WireMock** 
---

## Requirements

To build and run the project locally you need:

- **JDK**: 25  
- **Maven**: 3.9.x or newer  
- **Docker**:
    - Required if you want to run **Testcontainers** based integration tests and run application locally with use of docker compose
- **Git** 

---

## Local Run

### 1. Build with Maven

```bash
mvn clean install
```

### 2. Setup DB, Kafka and WireMock for Mastercard in container, wait for command to finish downloading and starting images
```bash
docker compose up
```

### 3. Run the app
```bash
mvn spring-boot:run
```

### 4. Documentation
[Swagger UI](http://localhost:1234/swagger-ui/index.html)

[Kafka UI](http://localhost:8080)

### WireMock Mastercard Mock

The service includes WireMock mocks simulating Mastercard's card generation API with three test scenarios:

#### **Happy Path (Green)**
- **Trigger**: Include `green` in the cardholder name (e.g., "Green Holder")
- **Response**: Returns HTTP 200 with successful card generation payload
- **Use Case**: Test successful card creation workflows

#### **Error Scenario (Red)**
- **Trigger**: Include `red` in the cardholder name (e.g., "Red Holder")
- **Response**: Returns HTTP 503 with validation error response
- **Use Case**: Test error handling and validation failure workflows

#### **Default Case**
- **Trigger**: Any cardholder name without `green` or `red` keywords
- **Response**: Returns HTTP 404 with not found message

## Problems

Due to IntelliJ IDEA open issue /https://youtrack.jetbrains.com/projects/IDEA/issues/IDEA-383312/Spock-Invalid-test-configuration-creation-from-editor-for-groovy-5

run tests from either run config or mvn, for now I do not want to include embedded solution
