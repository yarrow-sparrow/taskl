package com.github.straightth.mapper.project;

import com.github.straightth.dto.response.ProjectShortResponse;
import java.util.Collection;

public interface ProjectMapperEnricher {

    Collection<ProjectShortResponse> userToProjectShortResponse(String userId);
}
