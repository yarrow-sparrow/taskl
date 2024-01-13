package com.github.straightth.dto.request;

import com.github.straightth.domain.TaskStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateTaskRequest {

    @Size(min = 1, max = 30, message = "{taskl.validation.task.name-length}")
    @Nullable
    String name;

    @Size(min = 1, max = 300, message = "{taskl.validation.task.description-length}")
    @Nullable
    String description;

    @Nullable
    TaskStatus status;

    @Nullable
    String assigneeUserId;

    @PositiveOrZero(message = "{taskl.validation.task.story-points.positive-or-zero}")
    @Nullable
    Double storyPoints;
}
