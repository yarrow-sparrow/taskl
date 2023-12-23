package com.github.straightth.dto.request;

import static com.github.straightth.util.Constants.Project.DESCRIPTION_MAX_LENGTH;
import static com.github.straightth.util.Constants.Project.DESCRIPTION_MIN_LENGTH;
import static com.github.straightth.util.Constants.Project.NAME_MAX_LENGTH;
import static com.github.straightth.util.Constants.Project.NAME_MIN_LENGTH;

import com.github.straightth.util.Constants;
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

    @Size(
            min = NAME_MIN_LENGTH,
            max = NAME_MAX_LENGTH,
            message = "Project name should be between "
                    + NAME_MIN_LENGTH + " and "
                    + NAME_MAX_LENGTH + " characters"
    )
    @NotNull
    @Builder.Default
    String name = Constants.Project.DEFAULT_NAME;

    @Size(
            min = DESCRIPTION_MIN_LENGTH,
            max = DESCRIPTION_MAX_LENGTH,
            message = "Project description should be between "
                    + DESCRIPTION_MIN_LENGTH + " and "
                    + DESCRIPTION_MAX_LENGTH + " characters"
    )
    @NotNull
    @Builder.Default
    String description = Constants.Project.DEFAULT_DESCRIPTION;

    @NotNull
    @Builder.Default
    Collection<String> memberUserIds = List.of();
}
