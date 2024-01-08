package com.github.straightth.service.user;

import com.github.straightth.domain.User;
import com.github.straightth.exception.ApplicationError;
import com.github.straightth.exception.ErrorFactory;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.service.access.AbstractAccessService;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserAccessService extends AbstractAccessService<User, String, ApplicationError> {

    private final UserRepository userRepository;

    @Override
    public Function<Collection<String>, Collection<User>> defaultAccessFunction() {
        return userRepository::findAllById;
    }

    /**
     * We're considering that any user could be able to access any other user's profile no matter what
     */
    @Override
    public Function<Collection<String>, Collection<User>> securedAccessFunction() {
        return userRepository::findAllById;
    }

    @Override
    public Supplier<ApplicationError> notFoundExceptionSupplier() {
        return ErrorFactory.get()::userNotFound;
    }
}
