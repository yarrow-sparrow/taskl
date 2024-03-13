package com.github.yarrow.sparrow.dto.request;

import com.github.yarrow.sparrow.util.Constants;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class SignInRequestDeprecated {

    @Email(regexp = Constants.Regexp.EMAIL_REGEXP, message = "{taskl.validation.user.email.pattern}")
    String email;
    String password;
}
