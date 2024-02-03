package com.github.straightth.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ErrorResponse {

    String code;
    int httpStatus;
    String description;
    String message;
}
