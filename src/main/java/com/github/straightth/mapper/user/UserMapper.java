package com.github.straightth.mapper.user;

import com.github.straightth.domain.User;
import com.github.straightth.dto.response.UserResponse;
import com.github.straightth.dto.response.UserShortResponse;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserShortResponse userToUserShortResponse(User user);

    Collection<UserShortResponse> usersToUserShortResponses(Collection<User> users);

    UserResponse userToUserResponse(User user);
}
