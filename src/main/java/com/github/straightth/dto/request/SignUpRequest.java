package com.github.straightth.dto.request;

import com.github.straightth.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SignUpRequest {

    @Size(min = 1, max = 30, message = "{taskl.validation.user.username-length}")
    String username;
    @Email(regexp = Constants.Regexp.EMAIL_REGEXP, message = "{taskl.validation.user.email-format}")
    String email;
    @Pattern(regexp = Constants.Regexp.PASSWORD_REGEXP, message = "{taskl.validation.user.password-requirements}")
    String password;
}
