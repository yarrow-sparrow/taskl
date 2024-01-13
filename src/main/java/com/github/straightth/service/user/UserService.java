package com.github.straightth.service.user;

import com.github.straightth.dto.request.UpdateUserHimselfRequest;
import com.github.straightth.dto.response.UserResponse;

public interface UserService {

    UserResponse getUserById(String userId);

    UserResponse getUserHimself();

    UserResponse updateUserHimself(UpdateUserHimselfRequest request);
}
