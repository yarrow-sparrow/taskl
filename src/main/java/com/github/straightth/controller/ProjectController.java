package com.github.straightth.controller;

import com.github.straightth.dto.request.CreateProjectRequest;
import com.github.straightth.dto.request.UpdateProjectRequest;
import com.github.straightth.dto.response.ProjectResponse;
import com.github.straightth.dto.response.ProjectShortResponse;
import com.github.straightth.service.project.ProjectService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(@RequestBody @Valid Optional<CreateProjectRequest> request) {
        return projectService.createProject(request.orElse(CreateProjectRequest.builder().build()));
    }

    @GetMapping
    public Collection<ProjectShortResponse> getUserProjects() {
        return projectService.getUserProjects();
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getProjectById(@PathVariable String projectId) {
        return projectService.getProjectById(projectId);
    }

    @PutMapping("/{projectId}")
    public ProjectResponse updateProjectById(
            @PathVariable String projectId,
            @RequestBody @Valid Optional<UpdateProjectRequest> request
    ) {
        return projectService.updateProjectById(projectId, request.orElse(UpdateProjectRequest.builder().build()));
    }
}
