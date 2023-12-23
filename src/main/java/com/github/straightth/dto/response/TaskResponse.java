package com.github.straightth.dto.response;

import com.github.straightth.domain.TaskStatus;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class TaskResponse {

    String id;
    String projectId;
    String name;
    String description;
    UserShortResponse assigneeUser;
    TaskStatus status;
    Double storyPoints;
    Integer daysLeft;
}
