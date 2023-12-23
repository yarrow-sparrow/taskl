package com.github.straightth.service.project;

import com.github.straightth.dto.request.CreateProjectRequest;
import com.github.straightth.dto.request.UpdateProjectRequest;
import com.github.straightth.dto.response.ProjectResponse;
import com.github.straightth.dto.response.ProjectShortResponse;
import com.github.straightth.mapper.project.ProjectMapper;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.service.user.UserAccessService;
import com.github.straightth.util.SecurityUtil;
import java.util.ArrayList;
import java.util.Collection;
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
    public ProjectResponse createProject(CreateProjectRequest request) {
        var project = projectMapper.createProjectRequestToProject(request);

        var memberUserIds = new ArrayList<>(request.getMemberUserIds());
        userPresenceService.validatePresenceOrThrow(memberUserIds);

        // Adding user to created project
        var currentUserId = SecurityUtil.getCurrentUserId();
        memberUserIds.add(currentUserId);
        project.setMemberUserIds(memberUserIds);

        project = projectRepository.save(project);
        return projectMapper.projectToProjectResponse(project);
    }

    @Override
    public Collection<ProjectShortResponse> getUserProjects() {
        var currentUserId = SecurityUtil.getCurrentUserId();
        var projects = projectRepository.findProjectsByMemberUserIdsContains(currentUserId);
        return projectMapper.projectsToProjectShortResponses(projects);
    }

    @Override
    public ProjectResponse getProjectById(String projectId) {
        var project = projectAccessService.getPresentOrThrowSecured(projectId);
        return projectMapper.projectToProjectResponse(project);
    }

    @Transactional
    @Override
    public ProjectResponse updateProjectById(String projectId, UpdateProjectRequest request) {
        var project = projectAccessService.getPresentOrThrowSecured(projectId);

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
