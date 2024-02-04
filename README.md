# Taskl

## Overview

Taskl is a Java-based task manager RESTful API designed to demonstrate high-quality software development practices.
It emphasizes simplicity ("keep it simple stupid" principle), robust security, and a rich infrastructure.
Despite having only three domain entities, Taskl is engineered for easy expansion to any business requirements while preserving product and its codebase quality.

## Getting Started

1. To launch Taskl, execute `docker-compose up` from project's root directory.
2. Once running, you can access the Springdoc Swagger UI at http://localhost:8080/api/swagger-ui/index.html to interact with the API.

## Technologies

- **Spring Boot**: Main framework for API functionality.
- **Lombok**: Used to minimize boilerplate; transition to Kotlin for better interoperability is planned.
- **MapStruct**: Facilitates the mapping between entity and DTO layers.
- **Embedded MongoDB**: Supports transactional operations in a multi-cluster setup for local development and testing.
- **Guava**: Provides additional utilities, such as multimap.
- **Mustache**: Enables dynamic error message templating.
- **JUnit & AssertJ**: Allows to write expressive Java tests.
- **Docker Compose**: Simplifies infrastructure setup and project start-up.

## Key Features

### Error Factory

[The Error Factory](https://github.com/straightth/taskl/blob/main/src/main/java/com/github/straightth/exception/ErrorFactory.java#L8) streamlines the creation of standardized business logic errors.
By making error generation declarative, it simplifies the development process.
Below is an example of defining an error template:
```java
@ErrorTemplate(
    id = "taskl.api.error.validation-failed",
    httpStatus = HttpStatus.BAD_REQUEST,
    summary = "Validation failed",
    message = "{{message}}"
)
ApplicationError validationFailed(String message);
```

This template produces an [ApplicationError](https://github.com/straightth/taskl/blob/main/src/main/java/com/github/straightth/exception/ApplicationError.java#L14) object:
```java
public class ApplicationError {
    String code;
    HttpStatus httpStatus;
    String summary;
    String message;
}
```

Error Factory is implemented as a singleton to ensure that application-wide errors are consistently accessible.
To [create a specific error](https://github.com/straightth/taskl/blob/main/src/main/java/com/github/straightth/service/authentication/AuthenticationService.java#L29) `ErrorFactory.get().specificError()` has to be called:
```java
void signUp(SignUpRequest request) {
    if (userRepository.existsUserByEmail(request.getEmail())) {
        throw ErrorFactory.get().emailAlreadyInUse();
    }
    //More code
}
```

The centralization of error handling is further enhanced by a [common method](https://github.com/straightth/taskl/blob/main/src/main/java/com/github/straightth/controller/ExceptionHandlingAdvice.java#L27) that builds the response based on the generated error:
```java
@ExceptionHandler(ApplicationError.class)
public ResponseEntity<ErrorResponse> applicationErrorHandler(ApplicationError e) {
    return ResponseEntity.status(e.getHttpStatus().value()).body(e.toResponse());
}
```

This uniform approach to error management reduces boilerplate and ensures clarity and consistency in how errors are communicated throughout the application.

### Access Service

[The Access Service](https://github.com/straightth/taskl/blob/main/src/main/java/com/github/straightth/service/access/AccessService.java#L15) API empowers consistent and secure access to entities:
```java
EntityT getPresentOrThrow(IdT id);

Collection<EntityT> getPresentOrThrow(Collection<IdT> ids);
```

It also offers a Secured version of methods which allows to control entity visibility to user:
```java
EntityT getPresentOrThrowSecured(IdT id);
```

Well-tested base class allows user to implement [the service](https://github.com/straightth/taskl/blob/main/src/main/java/com/github/straightth/service/project/ProjectAccessService.java#L18) by defining only two access function and one factory method:
```java
@Override
public Function<Collection<String>, Collection<Project>> defaultAccessFunction() {
    return projectRepository::findAllById;
}

@Override
public Function<Collection<String>, Collection<Project>> securedAccessFunction() {
    return projectIds -> {
        var currentUserId = SecurityUtil.getCurrentUserId();
        return projectRepository.findProjectsByIdInAndMemberUserIdsContains(projectIds, currentUserId);
    };
}

@Override
public Supplier<ApplicationError> notFoundExceptionSupplier() {
    return ErrorFactory.get()::projectNotFound;
}
```

### Embedded Multi-Cluster MongoDB

This setup enhances developer experience by simplifying local setup and supporting transactional operations without the need for a Docker daemon.

### CI/CD with Checkstyle

GitHub's workflows are used to maintain code quality, running tests and lint checks on every pull request to ensure the health of the codebase.

## Testing Strategy

- Test suite achieves 87% line coverage and 91% method coverage.
- Integration tests are prioritized over pure unit tests to ensure reliability and system integrity.
- Each test running in a separate transaction for isolation.

## Roadmap

- Migrate from Lombok to Kotlin for enhanced language features and interoperability.
- Transition build scripts to Kotlin DSL for Gradle.
- Integrate SonarQube for continuous code quality checks.
- Implement a Jacoco workflow for automated coverage reporting.
- Configure environment-specific database setups, including a standalone MongoDB cluster.
- Plan deployment to Google Cloud and manage infrastructure as code with Terraform.
- Expand OAuth 2.0 registration options for enhanced security.
