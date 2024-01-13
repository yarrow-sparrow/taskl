package com.github.straightth.mapper.user;

import com.github.straightth.dto.response.UserShortResponse;
import java.util.Collection;


public interface UserMapperEnricher {

    UserShortResponse userToUserShortResponse(String userId);

    Collection<UserShortResponse> usersToUserShortResponses(Collection<String> memberUserIds);
}
