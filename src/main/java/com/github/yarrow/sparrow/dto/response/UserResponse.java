package com.github.yarrow.sparrow.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserResponse {

    String id;
    String username;
    String initials;
    String email;
    String phoneNumber;
}
