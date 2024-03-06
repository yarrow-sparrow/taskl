package com.github.yarrow.sparrow.service.project;

import com.github.yarrow.sparrow.dto.request.CreateProjectRequest;
import com.github.yarrow.sparrow.dto.request.UpdateProjectRequest;
import com.github.yarrow.sparrow.dto.response.ProjectResponse;
import com.github.yarrow.sparrow.dto.response.ProjectShortResponse;
import com.github.yarrow.sparrow.mapper.project.ProjectMapper;
import com.github.yarrow.sparrow.repository.ProjectRepository;
import com.github.yarrow.sparrow.service.user.UserAccessService;
import com.github.yarrow.sparrow.util.SecurityUtil;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserAccessService userPresenceService;
    private final ProjectMapper projectMapper;
    private final ProjectAccessService projectAccessService;

    @Transactional
    @Override
    public ProjectResponse saveProject(CreateProjectRequest request) {
        var project = projectMapper.createProjectRequestToProject(request);

        //Capitalizing key
        project.setKey(project.getKey().toUpperCase());

        //Adding current user to created project
        var currentUserId = SecurityUtil.getCurrentUserId();
        project.setMemberUserIds(List.of(currentUserId));

        project = projectRepository.save(project);
        return projectMapper.projectToProjectResponse(project);
    }

    @Override
    public Collection<ProjectShortResponse> getUserProjects() {
        var currentUserId = SecurityUtil.getCurrentUserId();
        var projects = projectRepository.findAllByMemberUserIdsContains(currentUserId);
        return projectMapper.projectsToProjectShortResponses(projects);
    }

    @Override
    public ProjectResponse getProjectByIdOrKey(String projectIdOrKey) {
        var project = projectAccessService.getPresentOrThrowSecured(projectIdOrKey);
        return projectMapper.projectToProjectResponse(project);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectByIdOrKey(String projectIdOrKey, UpdateProjectRequest request) {
        //noinspection DuplicatedCode: similar fields and update approach with Task, but different entities
        var project = projectAccessService.getPresentOrThrowSecured(projectIdOrKey);

        var name = request.getName();
        if (StringUtils.isNotBlank(name)) {
            project.setName(name);
        }
        var description = request.getDescription();
        if (StringUtils.isNotBlank(description)) {
            project.setDescription(description);
        }
        var memberUserIds = request.getMemberUserIds();
        if (memberUserIds != null) {
            userPresenceService.validatePresenceOrThrow(memberUserIds);
            project.setMemberUserIds(memberUserIds);
        }

        projectRepository.save(project);
        return projectMapper.projectToProjectResponse(project);
    }
}
