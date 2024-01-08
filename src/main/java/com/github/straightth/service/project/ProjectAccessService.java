package com.github.straightth.service.project;

import com.github.straightth.domain.Project;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.service.access.AbstractAccessService;
import com.github.straightth.exception.ApplicationError;
import com.github.straightth.exception.ErrorFactory;
import com.github.straightth.util.SecurityUtil;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProjectAccessService extends AbstractAccessService<Project, String, ApplicationError> {

    private final ProjectRepository projectRepository;

    @Override
    public Function<Collection<String>, Collection<Project>> defaultAccessFunction() {
        return projectRepository::findAllById;
    }

    @Override
    public Function<Collection<String>, Collection<Project>> securedAccessFunction() {
        return projectIds -> {
            var currentUserId = SecurityUtil.getCurrentUserId();
            return projectRepository.findProjectsByIdInAndMemberUserIdsContains(projectIds, currentUserId);
        };
    }

    @Override
    public Supplier<ApplicationError> notFoundExceptionSupplier() {
        return ErrorFactory.get()::projectNotFound;
    }
}
