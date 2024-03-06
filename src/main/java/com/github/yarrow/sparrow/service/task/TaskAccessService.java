package com.github.yarrow.sparrow.service.task;

import com.github.yarrow.sparrow.domain.Project;
import com.github.yarrow.sparrow.domain.Task;
import com.github.yarrow.sparrow.exception.ApplicationError;
import com.github.yarrow.sparrow.exception.ErrorFactory;
import com.github.yarrow.sparrow.repository.ProjectRepository;
import com.github.yarrow.sparrow.repository.TaskRepository;
import com.github.yarrow.sparrow.service.access.AbstractAccessService;
import com.github.yarrow.sparrow.util.SecurityUtil;
import com.google.common.collect.MultimapBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskAccessService extends AbstractAccessService<Task, String, ApplicationError> {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Override
    public Function<Collection<String>, Collection<Task>> defaultAccessFunction() {
        return taskRepository::findAllById;
    }

    @Override
    public Function<Collection<String>, Collection<Task>> securedAccessFunction() {
        return taskIds -> {
            var tasks = taskRepository.findAllById(taskIds);
            var tasksByProjectId = MultimapBuilder.hashKeys().arrayListValues().<String, Task>build();
            tasks.forEach(t -> tasksByProjectId.put(t.getProjectId(), t));

            var currentUserId = SecurityUtil.getCurrentUserId();
            var accessibleProjects = projectRepository.findAllByIdInAndMemberUserIdsContains(
                    tasksByProjectId.keySet(),
                    currentUserId
            );
            var accessibleProjectIds = accessibleProjects.stream().map(Project::getId).collect(Collectors.toSet());

            var accessibleTasks = new ArrayList<Task>();
            for (var projectId : tasksByProjectId.keySet()) {
                if (accessibleProjectIds.contains(projectId)) {
                    accessibleTasks.addAll(tasksByProjectId.get(projectId));
                }
            }

            return accessibleTasks;
        };
    }

    @Override
    public Supplier<ApplicationError> notFoundExceptionSupplier() {
        return ErrorFactory.get()::taskNotFound;
    }
}
