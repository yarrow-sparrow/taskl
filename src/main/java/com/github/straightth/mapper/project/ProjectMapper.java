package com.github.straightth.mapper.project;

import com.github.straightth.domain.Project;
import com.github.straightth.dto.request.CreateProjectRequest;
import com.github.straightth.dto.response.ProjectResponse;
import com.github.straightth.dto.response.ProjectShortResponse;
import com.github.straightth.mapper.user.UserMapperEnricher;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = UserMapperEnricher.class)
public interface ProjectMapper {

    Collection<ProjectShortResponse> projectsToProjectShortResponses(Collection<Project> projects);
    @Mapping(source = "memberUserIds", target = "memberUsers")
    ProjectResponse projectToProjectResponse(Project project);
    Project createProjectRequestToProject(CreateProjectRequest request);
}
