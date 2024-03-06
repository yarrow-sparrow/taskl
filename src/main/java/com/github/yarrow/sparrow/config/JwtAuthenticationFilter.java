package com.github.yarrow.sparrow.config;

import com.github.yarrow.sparrow.repository.UserRepository;
import com.github.yarrow.sparrow.service.authentication.CustomUserDetails;
import com.github.yarrow.sparrow.service.authentication.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        var authHeader = request.getHeader("Authorization");
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var jwt = authHeader.substring(7);
        var userId = jwtService.extractId(jwt);

        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        var user = userOptional.get();
        if (SecurityContextHolder.getContext().getAuthentication() == null
                && StringUtils.isNotEmpty(userId)
                && jwtService.isTokenValid(jwt, user)
        ) {
            var token = new UsernamePasswordAuthenticationToken(
                    CustomUserDetails.of(user),
                    null,
                    List.of()
            );
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            var context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(token);
            SecurityContextHolder.setContext(context);
        }
        filterChain.doFilter(request, response);
    }
}
