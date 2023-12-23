package com.github.straightth.service.authentication;

import com.github.straightth.domain.User;
import java.util.Collection;
import java.util.List;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Value
public class CustomUserDetails implements UserDetails {

    String id;
    String email;
    String password;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static CustomUserDetails of(User user) {
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword());
    }
}
