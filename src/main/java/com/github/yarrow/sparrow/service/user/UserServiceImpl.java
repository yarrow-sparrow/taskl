package com.github.yarrow.sparrow.service.user;

import com.github.yarrow.sparrow.dto.request.UpdateUserHimselfRequest;
import com.github.yarrow.sparrow.dto.response.UserResponse;
import com.github.yarrow.sparrow.exception.ErrorFactory;
import com.github.yarrow.sparrow.mapper.user.UserMapper;
import com.github.yarrow.sparrow.repository.UserRepository;
import com.github.yarrow.sparrow.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserAccessService userPresenceService;
    private final PasswordEncoder passwordEncoder;

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
        var email = request.getEmail();
        if (StringUtils.isNotBlank(email)) {
            if (userRepository.existsUserByEmail(email)) {
                throw ErrorFactory.get().emailAlreadyInUse();
            }
            user.setEmail(email);
        }
        var password = request.getPassword();
        if (StringUtils.isNotBlank(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }
        var phoneNumber = request.getPhoneNumber();
        if (StringUtils.isNotBlank(phoneNumber)) {
            user.setPhoneNumber(phoneNumber);
        }

        user = userRepository.save(user);
        return userMapper.userToUserResponse(user);
    }
}
