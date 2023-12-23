package com.github.straightth.dto.request;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateUserHimselfRequest {

    @Builder.Default
    String username = null;
    @Builder.Default
    String telegram = null;
    @Builder.Default
    String role = null;
    @Builder.Default
    String phoneNumber = null;
}
