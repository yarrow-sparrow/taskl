package com.github.straightth.dto.response;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class ProjectShortResponse {

    String id;
    String name;
    String description;
}
