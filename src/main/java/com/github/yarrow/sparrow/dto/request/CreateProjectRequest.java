package com.github.yarrow.sparrow.dto.request;

import static com.github.yarrow.sparrow.util.Constants.Regexp.KEY_REGEXP;

import com.github.yarrow.sparrow.dto.request.validation.FirstGroup;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@GroupSequence({
        FirstGroup.class,
        CreateProjectRequest.class,
})
public class CreateProjectRequest {

    @NotNull(message = "{taskl.validation.project.name.null}", groups = FirstGroup.class)
    @Size(min = 1, max = 30, message = "{taskl.validation.project.name.length}")
    @Builder.Default
    String name = null;

    @NotNull(message = "{taskl.validation.project.key.null}", groups = FirstGroup.class)
    @Size(min = 2, max = 20, message = "{taskl.validation.project.key.length}", groups = FirstGroup.class)
    @Pattern(regexp = KEY_REGEXP, message = "{taskl.validation.project.key.pattern}")
    @Builder.Default
    String key = null;

    @NotNull
    @Builder.Default
    String description = "";
}
