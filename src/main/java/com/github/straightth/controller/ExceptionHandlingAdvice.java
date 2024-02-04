package com.github.straightth.controller;

import com.github.straightth.dto.response.ErrorResponse;
import com.github.straightth.exception.ApplicationError;
import com.github.straightth.exception.ErrorFactory;
import com.google.common.base.Throwables;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingAdvice {

    /**
     * Here we're getting http code from threw error so only ResponseEntity is needed
     */
    @ExceptionHandler(ApplicationError.class)
    public ResponseEntity<ErrorResponse> applicationErrorHandler(ApplicationError e) {
        return ResponseEntity.status(e.getHttpStatus().value()).body(e.toResponse());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse missingServletRequestParameterExceptionHandler() {
        return ErrorFactory.get().badRequest().toResponse();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse httpMessageNotReadableException() {
        return ErrorFactory.get().badRequest().toResponse();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        var message = "Bad request";
        var fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null) {
            var fieldErrorMessage = fieldError.getDefaultMessage();
            if (StringUtils.isNotBlank(fieldErrorMessage)) {
                message = fieldErrorMessage;
            }
        }

        return ErrorFactory.get().validationFailed(message).toResponse();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse constraintViolationExceptionHandler(ConstraintViolationException e) {
        var message = e.getConstraintViolations().iterator().next().getMessage();
        return ErrorFactory.get().validationFailed(message).toResponse();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse unknownExceptionHandler(Exception e) {
        log.error("Unknown exception threw to user, message: {}, stacktrace {}",
                e.getMessage(),
                Throwables.getStackTraceAsString(e)
        );
        return ErrorFactory.get().internalServerError().toResponse();
    }
}
