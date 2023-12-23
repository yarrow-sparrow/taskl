package com.github.straightth.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SignUpRequest {

    @NotEmpty
    String username;
    @NotEmpty
    String email;
    @NotEmpty
    String password;
}
