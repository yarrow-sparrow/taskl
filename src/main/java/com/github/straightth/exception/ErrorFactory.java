package com.github.straightth.exception;

import org.springframework.http.HttpStatus;

/**
 * Error factory for reducing boilerplate of error handling code,
 *  make it declarative and support templating
 */
public interface ErrorFactory {

    static ErrorFactory get() {
        return ErrorFactoryInitializer.INSTANCE;
    }

    @ErrorTemplate(
            code = "taskl.api.error.validation-failed",
            httpStatus = HttpStatus.BAD_REQUEST,
            description = "Validation failed",
            message = "{{message}}"
    )
    ApplicationError validationFailed(String message);

    @ErrorTemplate(
            code = "taskl.api.error.bad-request",
            httpStatus = HttpStatus.BAD_REQUEST,
            description = "Bad request",
            message = "{{message}}"
    )
    ApplicationError badRequest(String message);

    @ErrorTemplate(
            code = "taskl.api.error.authentication.user-exists",
            httpStatus = HttpStatus.CONFLICT,
            description = "User already exists",
            message = "User already exists"
    )
    ApplicationError userAlreadyExists();

    @ErrorTemplate(
            code = "taskl.api.error.authentication.no-user-with-such-email",
            httpStatus = HttpStatus.BAD_REQUEST,
            description = "No user with such email",
            message = "No user with such email"
    )
    ApplicationError noUserWithSuchEmail();

    @ErrorTemplate(
            code = "taskl.api.error.authentication.incorrect-password",
            httpStatus = HttpStatus.BAD_REQUEST,
            description = "Incorrect password",
            message = "Incorrect password"
    )
    ApplicationError incorrectPassword();

    @ErrorTemplate(
            code = "taskl.api.error.user.not-found",
            httpStatus = HttpStatus.NOT_FOUND,
            description = "User not found",
            message = "User not found"
    )
    ApplicationError userNotFound();

    @ErrorTemplate(
            code = "taskl.api.error.project.not-found",
            httpStatus = HttpStatus.NOT_FOUND,
            description = "Project not found",
            message = "Project not found"
    )
    ApplicationError projectNotFound();

    @ErrorTemplate(
            code = "taskl.api.error.task.not-found",
            httpStatus = HttpStatus.NOT_FOUND,
            description = "Task not found",
            message = "Task not found"
    )
    ApplicationError taskNotFound();

    @ErrorTemplate(
            code = "taskl.api.error.unknown",
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            description = "Unknown error",
            message = "Unknown error"
    )
    ApplicationError internalServerError();
}
