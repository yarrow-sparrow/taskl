package com.github.straightth.authentication;

import com.github.straightth.domain.User;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.service.authentication.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class WithTokenSecurityContextFactory implements WithSecurityContextFactory<WithUserMock> {

    private final UserRepository userRepository;

    public SecurityContext createSecurityContext(WithUserMock withUser) {
        var user = User.builder()
                .email(withUser.email())
                .username(withUser.username())
                .password(withUser.encodedPassword())
                .build();
        user = userRepository.save(user);

        var context = SecurityContextHolder.createEmptyContext();

        var token = new UsernamePasswordAuthenticationToken(
                CustomUserDetails.of(user),
                null,
                List.of()
        );
        context.setAuthentication(token);
        return context;
    }
}
