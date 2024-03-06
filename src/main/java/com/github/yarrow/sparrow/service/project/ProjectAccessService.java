package com.github.yarrow.sparrow.service.project;

import com.github.yarrow.sparrow.domain.Project;
import com.github.yarrow.sparrow.exception.ApplicationError;
import com.github.yarrow.sparrow.exception.ErrorFactory;
import com.github.yarrow.sparrow.repository.ProjectRepository;
import com.github.yarrow.sparrow.service.access.AbstractAccessService;
import com.github.yarrow.sparrow.util.SecurityUtil;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectAccessService extends AbstractAccessService<Project, String, ApplicationError> {

    private final ProjectRepository projectRepository;

    @Override
    public Function<Collection<String>, Collection<Project>> defaultAccessFunction() {
        return projectIds -> {
            //Querying by ids
            var projects = projectRepository.findAllById(projectIds);
            if (CollectionUtils.isNotEmpty(projects)) {
                return projects;
            }

            //If no projects by ids found, querying by keys
            return projectRepository.findAllByKeyIn(projectIds);
        };
    }

    @Override
    public Function<Collection<String>, Collection<Project>> securedAccessFunction() {
        return projectIds -> {
            var currentUserId = SecurityUtil.getCurrentUserId();

            //Querying by ids
            var projects = projectRepository.findAllByIdInAndMemberUserIdsContains(projectIds, currentUserId);
            if (CollectionUtils.isNotEmpty(projects)) {
                return projects;
            }

            //If no projects by ids found, querying by keys
            return projectRepository.findAllByKeyInAndMemberUserIdsContains(projectIds, currentUserId);
        };
    }

    @Override
    public Supplier<ApplicationError> notFoundExceptionSupplier() {
        return ErrorFactory.get()::projectNotFound;
    }
}
