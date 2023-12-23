package com.github.straightth.service.user;

import com.github.straightth.dto.request.UpdateUserHimselfRequest;
import com.github.straightth.dto.response.UserHimselfResponse;
import com.github.straightth.dto.response.UserResponse;
import com.github.straightth.dto.response.UserShortResponse;
import java.util.Collection;

public interface UserService {

    Collection<UserShortResponse> getUsers();
    UserResponse getUserById(String userId);
    UserHimselfResponse getUserHimself();
    UserHimselfResponse updateUserHimself(UpdateUserHimselfRequest request);
}
