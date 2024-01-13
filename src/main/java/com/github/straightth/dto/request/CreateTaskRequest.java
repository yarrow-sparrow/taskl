package com.github.straightth.dto.request;

import com.github.straightth.domain.TaskStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateTaskRequest {

    @NotNull
    String projectId;

    @Size(min = 1, max = 30, message = "{taskl.validation.task.name-length}")
    @NotNull
    @Builder.Default
    String name = "New task";

    @Size(min = 1, max = 300, message = "{taskl.validation.task.description-length}")
    @NotNull
    @Builder.Default
    String description = "You can fill your description here";

    @NotNull
    @Builder.Default
    TaskStatus status = TaskStatus.BACKLOG;

    @Builder.Default
    String assigneeUserId = null;

    @PositiveOrZero(message = "{taskl.validation.task.story-points.positive-or-zero}")
    @Builder.Default
    Double storyPoints = 0d;
}
