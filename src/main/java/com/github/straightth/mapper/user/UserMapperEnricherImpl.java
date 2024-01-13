package com.github.straightth.mapper.user;

import com.github.straightth.dto.response.UserShortResponse;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.service.user.UserAccessService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * User and Project mapper services are using lazy initialization to overcome circular dependency
 * It happens, because we're displaying users in the projects at the same time as mutual projects at user's page
 */
@Service
@RequiredArgsConstructor(onConstructor_ = {
        @Autowired,
        @Lazy
})
public class UserMapperEnricherImpl implements UserMapperEnricher {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserAccessService userPresenceService;

    @Override
    public UserShortResponse userToUserShortResponse(String userId) {
        if (userId == null) {
            return null;
        }
        var presentUser = userPresenceService.getPresentOrThrow(userId);
        return userMapper.userToUserShortResponse(presentUser);
    }

    @Override
    public Collection<UserShortResponse> usersToUserShortResponses(Collection<String> userIds) {
        var users = userRepository.findAllById(userIds);
        return userMapper.usersToUserShortResponses(users);
    }
}
