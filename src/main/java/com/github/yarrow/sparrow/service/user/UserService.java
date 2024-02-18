package com.github.yarrow.sparrow.service.user;

import com.github.yarrow.sparrow.dto.request.UpdateUserHimselfRequest;
import com.github.yarrow.sparrow.dto.response.UserResponse;

public interface UserService {

    UserResponse getUserById(String userId);

    UserResponse getUserHimself();

    UserResponse updateUserHimself(UpdateUserHimselfRequest request);
}
