package com.github.yarrow.sparrow.mapper.project;

import com.github.yarrow.sparrow.domain.Project;
import com.github.yarrow.sparrow.dto.request.CreateProjectRequest;
import com.github.yarrow.sparrow.dto.response.ProjectResponse;
import com.github.yarrow.sparrow.dto.response.ProjectShortResponse;
import com.github.yarrow.sparrow.mapper.user.UserMapperEnricher;
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

    @Mapping(target = "id", ignore = true)
    Project createProjectRequestToProject(CreateProjectRequest request);
}
