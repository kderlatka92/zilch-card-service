# Zilch Card Service
## Overview
Zilch Card Service is a simplified demo card service that demonstrates simplified card creation process.  
For the purpose of this exercise it intentionally uses bleeding‑edge versions of the platform: the latest Java, Spring Boot and supporting libraries.  
The test suite is based on Spock, with Testcontainers and WireMock.

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

**Documentation**

- **SpringDoc OpenAPI** 

**Testing**

- **Spock Framework** 
- **Groovy** 5.x 
- **Spring Boot Test** 
- **Testcontainers**:
    - PostgreSQL module for containerized database tests
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

### 2. Setup DB in container, wait for command to finish downloading and starting DB image
```bash
docker compose up
```

### 3. Run the app
```bash
mvn spring-boot:run
```

### 4. Documentation
[Swagger UI](http://localhost:8080/swagger-ui/index.html)


## Problems

Due to IntelliJ IDEA open issue /https://youtrack.jetbrains.com/projects/IDEA/issues/IDEA-383312/Spock-Invalid-test-configuration-creation-from-editor-for-groovy-5

run tests from either run config or mvn
