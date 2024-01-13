package com.github.straightth.dto.response;

import java.util.Collection;
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
