package com.github.straightth.exception;

import org.springframework.http.HttpStatus;

/**
 * Error factory for reducing boilerplate of error handling code, make it declarative and support templating
 */
public interface ErrorFactory {

    static ErrorFactory get() {
        return ErrorFactoryInitializer.INSTANCE;
    }

    @ErrorTemplate(
            id = "taskl.api.error.bad-request",
            httpStatus = HttpStatus.BAD_REQUEST,
            summary = "Bad request",
            message = "Bad request"
    )
    ApplicationError badRequest();

    @ErrorTemplate(
            id = "taskl.api.error.validation-failed",
            httpStatus = HttpStatus.BAD_REQUEST,
            summary = "Validation failed",
            message = "{{message}}"
    )
    ApplicationError validationFailed(String message);

    @ErrorTemplate(
            id = "taskl.api.error.authentication.email-already-in-use",
            httpStatus = HttpStatus.CONFLICT,
            summary = "Email already in use",
            message = "Email already in use"
    )
    ApplicationError emailAlreadyInUse();

    @ErrorTemplate(
            id = "taskl.api.error.authentication.no-user-with-such-email",
            httpStatus = HttpStatus.BAD_REQUEST,
            summary = "No user with such email",
            message = "No user with such email"
    )
    ApplicationError noUserWithSuchEmail();

    @ErrorTemplate(
            id = "taskl.api.error.authentication.incorrect-password",
            httpStatus = HttpStatus.BAD_REQUEST,
            summary = "Incorrect password",
            message = "Incorrect password"
    )
    ApplicationError incorrectPassword();

    @ErrorTemplate(
            id = "taskl.api.error.user.not-found",
            httpStatus = HttpStatus.NOT_FOUND,
            summary = "User not found",
            message = "User not found"
    )
    ApplicationError userNotFound();

    @ErrorTemplate(
            id = "taskl.api.error.project.not-found",
            httpStatus = HttpStatus.NOT_FOUND,
            summary = "Project not found",
            message = "Project not found"
    )
    ApplicationError projectNotFound();

    @ErrorTemplate(
            id = "taskl.api.error.task.not-found",
            httpStatus = HttpStatus.NOT_FOUND,
            summary = "Task not found",
            message = "Task not found"
    )
    ApplicationError taskNotFound();

    @ErrorTemplate(
            id = "taskl.api.error.task.invalid-assignee-nullify",
            httpStatus = HttpStatus.BAD_REQUEST,
            summary = "Bad request",
            message = "Assignee id must be not present or null if nullifyAssigneeId is true"
    )
    ApplicationError assigneeIdIsNotBlankOnNullify();

    @ErrorTemplate(
            id = "taskl.api.error.unknown",
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            summary = "Unknown error",
            message = "Unknown error"
    )
    ApplicationError internalServerError();
}
