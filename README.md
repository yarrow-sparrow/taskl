To-Do:
- Rich README (overview, technologies, how to start, implementation and key features in it, todo)

---

# Taskl

## Overview

Taskl is a task manager RESTful API written mostly in Java for demonstration purpose.
The application idea is to keep it simple stupid and focus on implementation quality, security and infrastructure richness.
As a part of it there are only 3 domain entities in which business logic lies within CRUD operations.
Current implementation allows to easily expand quantity of features to any business requirements maintaining quality of product due to well-prepared code and infrastructure base.

## How to run it?

Simply run `docker-compose up` then observe an API through Springdoc Swagger UI: http://localhost:8080/api/swagger-ui/index.html

## Technologies

- Spring Boot as a main framework for creating rich and robust API
- Lombok for reducing boilerplate, migration to Kotlin is planned
- MapStruct for mapping entity to DTO
- Embedded MongoDB for running transactional multi-cluster MongoDB on local startup and inside tests for simplicity
- Guava is used for multimap and other utils
- Mustache for templating error messages
- JUnit as a main framework for testing
- AssertJ for writing concise tests
- Docker Compose for convenience of infrastructure and start-up in click

## Tests

Pure unit-tests are omitted in favor of integration tests with embedded database because slight different in performance considered cheaper than reliability and integrity improvement.

## Key-features

TODO: describe each feature, add links to files

### Error factory

### Access service

### Multi cluster embedded Mongo

### CI/CD with Checkstyle

GitHub's workflows ensure that codebase and business logic are kept in healthy state by running tests and Checkstyle lint on every PR

## To-Do
- Lombok -> Kotlin for interoperability wih Kotlin
- Migrate Gradle to Kotlin
- Jakarta violation response formatting: https://blog.payara.fish/returning-beautiful-validation-error-messages-in-jakarta-rest-with-exception-mappers
- Prettify render of ConstraintViolationException and null exceptions
- Sonar workflow
- Jacoco workflow with automatic coverage count
- Environment-based configuration: standalone MongoDB cluster through Docker Compose
- Deploy to Google Cloud
- OAuth 2 security options
