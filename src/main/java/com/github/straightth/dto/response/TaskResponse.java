package com.github.straightth.dto.response;

import com.github.straightth.domain.TaskStatus;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class TaskResponse {

    @NotNull
    String id;
    @NotNull
    String projectId;
    @NotNull
    String name;
    @NotNull
    String description;
    @Nullable
    UserShortResponse assigneeUser;
    @NotNull
    TaskStatus status;
    @NotNull
    Double storyPoints;
}
