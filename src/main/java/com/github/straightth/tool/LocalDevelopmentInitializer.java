package com.github.straightth.tool;

import com.github.straightth.service.authentication.AuthenticationService;
import com.github.straightth.dto.request.SignInRequest;
import com.github.straightth.dto.request.SignUpRequest;
import com.github.straightth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("development")
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LocalDevelopmentInitializer {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostConstruct
    public void initLocalDevelopment() {
        var username = "local-development";
        var password = "local-development";
        var email = "local@development.com";

        if (!userRepository.existsUserByEmail(email)) {
            var request = SignUpRequest.builder().username(username).email(email).password(password).build();
            authenticationService.signUp(request);
        }

        var request = SignInRequest.builder().email(email).password(password).build();
        var token = authenticationService.signIn(request);

        log.info("Token for local development: {}", token.getToken());
    }
}
