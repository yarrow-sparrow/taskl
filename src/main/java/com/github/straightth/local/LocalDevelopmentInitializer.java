package com.github.straightth.local;

import com.github.straightth.dto.request.SignInRequest;
import com.github.straightth.dto.request.SignUpRequest;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.service.authentication.AuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Initializer for easier access to protected endpoints
 * Creates user and prints its token to logs
 */
@Slf4j
@Profile("development")
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LocalDevelopmentInitializer {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostConstruct
    public void generateToken() {
        var username = "local-developer";
        var password = "local-developer";
        var email = "local@development.com";

        if (!userRepository.existsUserByEmail(email)) {
            var request = SignUpRequest.builder().username(username).email(email).password(password).build();
            authenticationService.signUp(request);
        }

        var request = SignInRequest.builder().email(email).password(password).build();
        var token = authenticationService.signIn(request);

        log.info("Token for local start-up: {}", token.getToken());
    }
}
