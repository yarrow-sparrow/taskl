package com.github.straightth.authentication;

import com.github.straightth.dto.request.SignInRequest;
import com.github.straightth.dto.request.SignUpRequest;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.service.authentication.AuthenticationService;
import com.github.straightth.service.authentication.CustomUserDetails;
import com.github.straightth.service.authentication.JwtService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WithTokenSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    //TODO: get rid of client code
    public SecurityContext createSecurityContext(WithMockCustomUser withUser) {
        var jwt = authenticateUser(withUser);
        var email = jwtService.extractEmail(jwt);
        var user = userRepository.findByEmail(email).orElseThrow();

        var token = new UsernamePasswordAuthenticationToken(
                CustomUserDetails.of(user),
                null,
                List.of()
        );

        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        return context;
    }

    private String authenticateUser(WithMockCustomUser withUser) {
        var username = withUser.username();
        var email = withUser.email();
        var password = withUser.password();

        if (!userRepository.existsUserByEmail(email)) {
            var request = SignUpRequest.builder()
                    .username(username)
                    .email(email)
                    .password(password)
                    .build();
            authenticationService.signUp(request);
        }
        var request = SignInRequest.builder()
                .email(email)
                .password(password)
                .build();
        var response = authenticationService.signIn(request);
        return response.getToken();
    }
}
