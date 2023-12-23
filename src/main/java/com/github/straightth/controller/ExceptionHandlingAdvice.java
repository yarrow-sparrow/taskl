package com.github.straightth.controller;

import com.github.straightth.dto.response.ErrorResponse;
import com.github.straightth.exception.authentication.IncorrectPassword;
import com.github.straightth.exception.authentication.NoUserWithSuchEmail;
import com.github.straightth.exception.authentication.UserAlreadyExists;
import com.github.straightth.exception.project.ProjectNotFound;
import com.github.straightth.exception.task.TaskNotFound;
import com.github.straightth.exception.user.UserNotFound;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlingAdvice {

    @ExceptionHandler(UserAlreadyExists.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ErrorResponse userAlreadyExistsExceptionHandler() {
        return ErrorResponse.of("User with such email already exists");
    }

    @ExceptionHandler(NoUserWithSuchEmail.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse noUserWithSuchEmailExceptionHandler() {
        return ErrorResponse.of("No user with such email");
    }

    @ExceptionHandler(IncorrectPassword.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidPassword() {
        return ErrorResponse.of("Invalid password");
    }

    @ExceptionHandler(UserNotFound.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse userNotFoundExceptionHandler() {
        return ErrorResponse.of("User not found");
    }

    @ExceptionHandler(ProjectNotFound.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse projectNotFoundExceptionHandler() {
        return ErrorResponse.of("Project not found");
    }

    @ExceptionHandler(TaskNotFound.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse taskNotFoundExceptionHandler() {
        return ErrorResponse.of("Task not found");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        var defaultErrorResponse = ErrorResponse.of("Bad request");

        var fieldError = e.getBindingResult().getFieldError();
        if (fieldError == null) {
            return defaultErrorResponse;
        }

        var message = fieldError.getDefaultMessage();
        if (StringUtils.isBlank(message)) {
            return defaultErrorResponse;
        }

        return ErrorResponse.of(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationExceptionHandler(ConstraintViolationException e) {
        return ErrorResponse.of(e.getConstraintViolations().iterator().next().getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exceptionHandler(Exception e) {
        return ErrorResponse.of("Unknown error", e);
    }
}
