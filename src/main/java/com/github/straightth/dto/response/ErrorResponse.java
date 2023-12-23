package com.github.straightth.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

@Slf4j
@Value
@Builder
@Jacksonized
public class ErrorResponse {
    String message;

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }

    public static ErrorResponse of(String message, Exception e) {
        log.error(ExceptionUtils.getStackTrace(e));
        return new ErrorResponse(message);
    }
}
