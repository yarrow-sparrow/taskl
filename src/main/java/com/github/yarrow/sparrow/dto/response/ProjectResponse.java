package com.github.yarrow.sparrow.dto.response;

import java.time.Instant;
import java.util.Collection;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProjectResponse {

    String id;
    String key;
    String name;
    String description;
    Instant createdTs;
    Instant updatedTs;
    Collection<UserShortResponse> memberUsers;
}
