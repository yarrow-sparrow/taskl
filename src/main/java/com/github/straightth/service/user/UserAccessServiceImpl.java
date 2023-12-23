package com.github.straightth.service.user;

import com.github.straightth.domain.User;
import com.github.straightth.repository.UserRepository;
import java.util.Collection;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserAccessServiceImpl extends UserAccessService {

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
}
