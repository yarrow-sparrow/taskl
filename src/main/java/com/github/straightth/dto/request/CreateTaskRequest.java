package com.github.straightth.dto.request;

import com.github.straightth.domain.TaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateTaskRequest {

    @NotEmpty
    @Builder.Default
    String name = "New task";
    @NotEmpty
    @Builder.Default
    String description = "Description of new task";
    @NotNull
    @Builder.Default
    TaskStatus status = TaskStatus.BACKLOG;
    @Builder.Default
    String assigneeUserId = null;
    @Positive
    @Builder.Default
    Double storyPoints = 1d;
    @Builder.Default
    LocalDate deadline = LocalDate.now().plusDays(7);
}
