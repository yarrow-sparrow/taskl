package com.github.straightth.service.user;

import com.github.straightth.domain.User;
import com.github.straightth.exception.user.UserNotFound;
import com.github.straightth.service.access.AbstractAccessService;
import java.util.function.Supplier;

public abstract class UserAccessService extends AbstractAccessService<User, String, UserNotFound> {

    @Override
    public Supplier<UserNotFound> notFoundExceptionSupplier() {
        return UserNotFound::new;
    }
}
