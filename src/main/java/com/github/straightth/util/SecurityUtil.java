package com.github.straightth.util;

import com.github.straightth.service.authentication.CustomUserDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class SecurityUtil {

    public String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new NullPointerException("Authentication is null");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getId();
    }
}
