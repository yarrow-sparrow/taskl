package com.github.yarrow.sparrow.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.HttpStatus;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface ErrorTemplate {

    String id();

    HttpStatus httpStatus();

    String summary();

    String message();

    ApplicationError.Level level() default ApplicationError.Level.ERROR;
}
