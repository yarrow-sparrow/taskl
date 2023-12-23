package com.github.straightth.dto.request;

import com.github.straightth.domain.TaskStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateTaskRequest {

    @Nullable
    String name;
    @Nullable
    String description;
    @Nullable
    TaskStatus status;
    @Nullable
    String assigneeUserId;
    @Nullable
    @Min(0)
    Double storyPoints;
    @Nullable
    LocalDate deadline;
}
