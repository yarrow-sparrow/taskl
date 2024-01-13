package com.github.straightth.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateUserHimselfRequest {

    @Size(min = 1, max = 30, message = "{taskl.validation.user.username-length}")
    @Builder.Default
    String username = null;
    @Builder.Default
    String phoneNumber = null;
}
