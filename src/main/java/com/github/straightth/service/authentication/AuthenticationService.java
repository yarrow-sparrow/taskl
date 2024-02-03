package com.github.straightth.service.authentication;

import com.github.straightth.domain.User;
import com.github.straightth.dto.request.SignInRequest;
import com.github.straightth.dto.request.SignUpRequest;
import com.github.straightth.dto.response.AuthenticationResponse;
import com.github.straightth.exception.ErrorFactory;
import com.github.straightth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public void signUp(SignUpRequest request) {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw ErrorFactory.get().userAlreadyExists();
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public AuthenticationResponse signIn(SignInRequest request) {
        var email = request.getEmail();
        var password = request.getPassword();

        var user = userRepository.findByEmail(email).orElseThrow(ErrorFactory.get()::noUserWithSuchEmail);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw ErrorFactory.get().incorrectPassword();
        }
        authenticationManager.authenticate(authenticationToken(email, password));
        var jwt = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwt)
                .userId(user.getId())
                .build();
    }

    private UsernamePasswordAuthenticationToken authenticationToken(String email, String password) {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
