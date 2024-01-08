package com.github.straightth.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class CreateProjectRequest {

    @Size(min = 1, max = 30, message = "{taskl.validation.project.name-length}")
    @NotNull
    @Builder.Default
    String name = "New project";

    @Size(min = 1, max = 300, message = "{taskl.validation.project.description-length}")
    @NotNull
    @Builder.Default
    String description = "You can fill your description here";

    @NotNull
    @Builder.Default
    Collection<String> memberUserIds = List.of();
}
