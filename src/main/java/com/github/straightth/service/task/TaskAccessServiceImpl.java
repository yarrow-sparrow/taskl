package com.github.straightth.service.task;

import com.github.straightth.domain.Project;
import com.github.straightth.domain.Task;
import com.github.straightth.exception.task.TaskNotFound;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.repository.TaskRepository;
import com.github.straightth.util.SecurityUtil;
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
public class TaskAccessServiceImpl extends TaskAccessService {

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
            var accessibleProjects = projectRepository.findProjectsByIdInAndMemberUserIdsContains(
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
    public Supplier<TaskNotFound> notFoundExceptionSupplier() {
        return TaskNotFound::new;
    }
}
