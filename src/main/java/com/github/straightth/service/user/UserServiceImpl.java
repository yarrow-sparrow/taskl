package com.github.straightth.service.user;

import com.github.straightth.dto.request.UpdateUserHimselfRequest;
import com.github.straightth.dto.response.UserResponse;
import com.github.straightth.mapper.user.UserMapper;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserAccessService userPresenceService;

    @Override
    public UserResponse getUserById(String userId) {
        var user = userPresenceService.getPresentOrThrow(userId);
        return userMapper.userToUserResponse(user);
    }

    @Override
    public UserResponse getUserHimself() {
        var userId = SecurityUtil.getCurrentUserId();
        var user = userPresenceService.getPresentOrThrow(userId);
        return userMapper.userToUserResponse(user);
    }

    @Override
    public UserResponse updateUserHimself(UpdateUserHimselfRequest request) {
        var userId = SecurityUtil.getCurrentUserId();
        var user = userPresenceService.getPresentOrThrow(userId);

        var username = request.getUsername();
        if (StringUtils.isNotBlank(username)) {
            user.setUsername(username);
        }
        var phoneNumber = request.getPhoneNumber();
        if (StringUtils.isNotBlank(phoneNumber)) {
            user.setPhoneNumber(phoneNumber);
        }

        user = userRepository.save(user);
        return userMapper.userToUserResponse(user);
    }
}
