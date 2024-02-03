package com.github.straightth.dto.request;

import com.github.straightth.util.Constants;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SignInRequest {

    @Email(regexp = Constants.Regexp.EMAIL_REGEXP, message = "{taskl.validation.user.email-format}")
    String email;
    String password;
}
