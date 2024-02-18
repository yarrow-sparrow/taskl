package com.github.yarrow.sparrow.util;

import com.github.yarrow.sparrow.service.authentication.CustomUserDetails;
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
