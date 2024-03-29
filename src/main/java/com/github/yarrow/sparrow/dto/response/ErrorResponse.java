package com.github.yarrow.sparrow.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ErrorResponse {

    String code;
    int httpStatus;
    String summary;
    String message;
}
