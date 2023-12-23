package com.github.straightth.mapper.user;

import com.github.straightth.domain.User;
import com.github.straightth.dto.response.UserHimselfResponse;
import com.github.straightth.dto.response.UserResponse;
import com.github.straightth.dto.response.UserShortResponse;
import com.github.straightth.mapper.project.ProjectMapperEnricher;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = ProjectMapperEnricher.class)
public interface UserMapper {

    UserShortResponse userToUserShortResponse(User user);
    Collection<UserShortResponse> usersToUserShortResponses(Collection<User> users);
    @Mapping(source = "id", target = "mutualProjects")
    UserResponse userToUserResponse(User user);
    UserHimselfResponse userToUserHimselfResponse(User user);
}
