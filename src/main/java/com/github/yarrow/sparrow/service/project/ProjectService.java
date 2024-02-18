package com.github.yarrow.sparrow.service.project;

import com.github.yarrow.sparrow.dto.request.CreateProjectRequest;
import com.github.yarrow.sparrow.dto.request.UpdateProjectRequest;
import com.github.yarrow.sparrow.dto.response.ProjectResponse;
import com.github.yarrow.sparrow.dto.response.ProjectShortResponse;
import java.util.Collection;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

    Collection<ProjectShortResponse> getUserProjects();

    ProjectResponse getProjectById(String projectId);

    ProjectResponse updateProjectById(String projectId, UpdateProjectRequest request);
}
