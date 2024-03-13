package com.github.yarrow.sparrow.local;

import com.github.yarrow.sparrow.generated.model.SignInRequest;
import com.github.yarrow.sparrow.generated.model.SignUpRequest;
import com.github.yarrow.sparrow.repository.UserRepository;
import com.github.yarrow.sparrow.service.authentication.AuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TODO: UserDetailsServiceAutoConfiguration
 * Using generated security password: 4273114b-042b-4165-8018-7b3bd7e5de06
 * This generated password is for development use only. Your security configuration must be updated before running your application in production.
 * _
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
            var request = new SignUpRequest().username(username).email(email).password(password);
            authenticationService.signUp(request);
        }

        var request = new SignInRequest().email(email).password(password);
        var token = authenticationService.signIn(request);

        log.info("Token for local start-up: {}", token.getToken());
    }
}
