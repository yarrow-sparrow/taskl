package com.github.straightth.service.project;

import com.github.straightth.dto.request.CreateProjectRequest;
import com.github.straightth.dto.request.UpdateProjectRequest;
import com.github.straightth.dto.response.ProjectResponse;
import com.github.straightth.dto.response.ProjectShortResponse;
import java.util.Collection;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest request);

    Collection<ProjectShortResponse> getUserProjects();

    ProjectResponse getProjectById(String projectId);

    ProjectResponse updateProjectById(String projectId, UpdateProjectRequest request);
}
