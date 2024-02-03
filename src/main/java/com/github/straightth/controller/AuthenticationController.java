package com.github.straightth.controller;

import com.github.straightth.dto.request.SignInRequest;
import com.github.straightth.dto.request.SignUpRequest;
import com.github.straightth.dto.response.SignInResponse;
import com.github.straightth.service.authentication.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody @Valid SignUpRequest request) {
        authenticationService.signUp(request);
    }

    @PostMapping("/sign-in")
    public SignInResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }
}
