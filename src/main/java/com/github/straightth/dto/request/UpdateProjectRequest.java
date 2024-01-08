package com.github.straightth.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UpdateProjectRequest {

    @Size(min = 1, max = 30, message = "{taskl.validation.project.name-length}")
    @Nullable
    @Builder.Default
    String name = null;

    @Size(min = 1, max = 300, message = "{taskl.validation.project.description-length}")
    @Nullable
    @Builder.Default
    String description = null;

    @Nullable
    @Builder.Default
    Collection<String> memberUserIds = null;
}
