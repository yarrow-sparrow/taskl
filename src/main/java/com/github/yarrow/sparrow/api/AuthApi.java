package com.github.yarrow.sparrow.api;

import com.github.yarrow.sparrow.generated.api.AuthApiDelegate;
import com.github.yarrow.sparrow.generated.model.SignIn200Response;
import com.github.yarrow.sparrow.generated.model.SignInRequest;
import com.github.yarrow.sparrow.generated.model.SignUpRequest;
import com.github.yarrow.sparrow.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * TODO: delete DTO, fix tests, clean error messages etc.
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthApi implements AuthApiDelegate {

    private final AuthenticationService authenticationService;

    @Override
    public ResponseEntity<SignIn200Response> signIn(SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @Override
    public ResponseEntity<Void> signUp(SignUpRequest request) {
        authenticationService.signUp(request);
        return null;
    }
}
