package com.github.straightth.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserHimselfResponse {

    String id;
    String username;
    String email;
    String telegram;
    String role;
    String phoneNumber;
}
