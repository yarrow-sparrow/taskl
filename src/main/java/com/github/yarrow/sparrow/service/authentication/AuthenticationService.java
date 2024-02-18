package com.github.yarrow.sparrow.service.authentication;

import com.github.yarrow.sparrow.domain.User;
import com.github.yarrow.sparrow.dto.request.SignInRequest;
import com.github.yarrow.sparrow.dto.request.SignUpRequest;
import com.github.yarrow.sparrow.dto.response.SignInResponse;
import com.github.yarrow.sparrow.exception.ErrorFactory;
import com.github.yarrow.sparrow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthenticationService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsUserByEmail(request.getEmail())) {
            throw ErrorFactory.get().emailAlreadyInUse();
        }

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public SignInResponse signIn(SignInRequest request) {
        var email = request.getEmail();
        var password = request.getPassword();

        var user = userRepository.findByEmail(email).orElseThrow(ErrorFactory.get()::noUserWithSuchEmail);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw ErrorFactory.get().incorrectPassword();
        }
        authenticationManager.authenticate(authenticationToken(email, password));
        var jwt = jwtService.generateToken(user);

        return SignInResponse.builder()
                .token(jwt)
                .build();
    }

    private UsernamePasswordAuthenticationToken authenticationToken(String email, String password) {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
