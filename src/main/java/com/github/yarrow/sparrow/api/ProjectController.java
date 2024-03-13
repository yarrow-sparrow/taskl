package com.github.yarrow.sparrow.api;

import com.github.yarrow.sparrow.dto.request.CreateProjectRequest;
import com.github.yarrow.sparrow.dto.request.UpdateProjectRequest;
import com.github.yarrow.sparrow.dto.response.ProjectResponse;
import com.github.yarrow.sparrow.dto.response.ProjectShortResponse;
import com.github.yarrow.sparrow.service.project.ProjectService;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ProjectResponse saveProject(@RequestBody @Valid CreateProjectRequest request) {
        return projectService.saveProject(request);
    }

    @GetMapping
    public Collection<ProjectShortResponse> getUserProjects() {
        return projectService.getUserProjects();
    }

    @GetMapping("/{projectIdOrKey}")
    public ProjectResponse getProjectByIdOrKey(@PathVariable String projectIdOrKey) {
        return projectService.getProjectByIdOrKey(projectIdOrKey);
    }

    @PutMapping("/{projectIdOrKey}")
    public ProjectResponse updateProjectByIdOrKey(
            @PathVariable String projectIdOrKey,
            @RequestBody @Valid Optional<UpdateProjectRequest> request
    ) {
        return projectService.updateProjectByIdOrKey(
                projectIdOrKey,
                request.orElse(UpdateProjectRequest.builder().build())
        );
    }
}
