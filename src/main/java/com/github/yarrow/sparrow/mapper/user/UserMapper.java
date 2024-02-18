package com.github.yarrow.sparrow.mapper.user;

import com.github.yarrow.sparrow.domain.User;
import com.github.yarrow.sparrow.dto.response.UserResponse;
import com.github.yarrow.sparrow.dto.response.UserShortResponse;
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
