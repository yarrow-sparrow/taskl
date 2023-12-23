package com.github.straightth.mapper.project;

import com.github.straightth.dto.response.ProjectShortResponse;
import com.github.straightth.repository.ProjectRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = {
        @Autowired,
        @Lazy
})
public class ProjectMapperEnricherImpl implements ProjectMapperEnricher {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public Collection<ProjectShortResponse> userToProjectShortResponse(String userId) {
        var projects = projectRepository.findProjectsByMemberUserIdsContains(userId);
        return projectMapper.projectsToProjectShortResponses(projects);
    }
}
