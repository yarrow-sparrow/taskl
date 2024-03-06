package com.github.yarrow.sparrow.dto.request;

import com.github.yarrow.sparrow.util.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateUserHimselfRequest {

    @Size(min = 1, max = 30, message = "{taskl.validation.user.username.length}")
    @Builder.Default
    String username = null;
    @Email(regexp = Constants.Regexp.EMAIL_REGEXP, message = "{taskl.validation.user.email.pattern}")
    @Builder.Default
    String email = null;
    @Pattern(regexp = Constants.Regexp.PASSWORD_REGEXP, message = "{taskl.validation.user.password.requirements}")
    @Builder.Default
    String password = null;
    @Builder.Default
    String phoneNumber = null;
}
