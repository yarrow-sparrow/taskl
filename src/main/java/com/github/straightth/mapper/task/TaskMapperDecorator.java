package com.github.straightth.mapper.task;

import com.github.straightth.domain.Task;
import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import com.github.straightth.dto.response.UserShortResponse;
import com.github.straightth.mapper.user.UserMapper;
import com.github.straightth.service.user.UserAccessService;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Decorator is needed to fetch assignees efficiently in tasksToTaskResponses() through one query to the database
 */
@Component
@Primary
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskMapperDecorator implements TaskMapper {

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
        var assigneeUserIds = tasks.stream().map(Task::getAssigneeUserId).toList();
        var assigneeShortResponseByUserId = userPresenceService.getPresentOrThrow(assigneeUserIds).stream()
                .map(userMapper::userToUserShortResponse)
                .collect(Collectors.toMap(UserShortResponse::getId, Function.identity()));

        var taskResponses = delegate.tasksToTaskResponses(tasks);
        return taskResponses.stream()
                .map(r -> {
                    var userId = r.getAssigneeUser().getId();
                    var userShortResponse = assigneeShortResponseByUserId.get(userId);
                    return r.toBuilder().assigneeUser(userShortResponse).build();
                })
                .toList();
    }
}
