package com.github.yarrow.sparrow.mapper.user;

import com.github.yarrow.sparrow.dto.response.UserShortResponse;
import java.util.Collection;


public interface UserMapperEnricher {

    UserShortResponse userToUserShortResponse(String userId);

    Collection<UserShortResponse> usersToUserShortResponses(Collection<String> memberUserIds);
}
