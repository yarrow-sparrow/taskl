package com.github.straightth.authentication;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithTokenSecurityContextFactory.class)
public @interface WithUserMock {

    String username() default "username";

    String email() default "email@email.com";

    String encodedPassword() default "password";

    /**
     * This method has priority over encoded password because PasswordEncoder is applied to it
     */
    String rawPassword() default "";
}
