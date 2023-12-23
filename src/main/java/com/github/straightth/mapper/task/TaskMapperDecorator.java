package com.github.straightth.mapper.task;

import com.github.straightth.domain.Task;
import com.github.straightth.domain.User;
import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import com.github.straightth.mapper.user.UserMapper;
import com.github.straightth.service.user.UserAccessService;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Decorator is needed to fetch assignees efficiently in tasksToTaskResponses() through one query to the database
 */
@Component
@Primary
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskMapperDecorator implements TaskMapper {

    @Qualifier("delegate")
    private final TaskMapper delegate;
    private final UserAccessService userPresenceService;
    private final UserMapper userMapper;

    @Override
    public Task createTaskRequestToTask(CreateTaskRequest request) {
        return delegate.createTaskRequestToTask(request);
    }

    @Override
    public TaskResponse taskToTaskResponse(Task task) {
        return delegate.taskToTaskResponse(task);
    }

    @Override
    public Collection<TaskResponse> tasksToTaskResponses(Collection<Task> tasks) {
        var assigneeUserIdByTaskId = tasks.stream()
                .collect(Collectors.toMap(Task::getId, Task::getAssigneeUserId));

        var assigneeUserIds = assigneeUserIdByTaskId.values();
        var userById = userPresenceService.getPresentOrThrow(assigneeUserIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        var taskResponseBuilders = delegate.tasksToTaskResponses(tasks);
        return taskResponseBuilders.stream()
                .map(r -> {
                    var taskId = r.getId();
                    var builder = taskResponseBuilder(r);
                    var assigneeUserId = assigneeUserIdByTaskId.get(taskId);
                    var user = userById.get(assigneeUserId);
                    var userShortResponse = userMapper.userToUserShortResponse(user);
                    return builder.assigneeUser(userShortResponse).build();
                })
                .toList();
    }

    //TODO: fixit
    private TaskResponse.TaskResponseBuilder taskResponseBuilder(TaskResponse response) {
        return TaskResponse.builder()
                .id(response.getId())
                .projectId(response.getProjectId())
                .name(response.getName())
                .description(response.getDescription())
                .assigneeUser(response.getAssigneeUser())
                .status(response.getStatus())
                .storyPoints(response.getStoryPoints())
                .daysLeft(response.getDaysLeft());
    }
}
