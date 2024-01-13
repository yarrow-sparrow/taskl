package com.github.straightth.dto.request;

import jakarta.validation.constraints.NotEmpty;
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
