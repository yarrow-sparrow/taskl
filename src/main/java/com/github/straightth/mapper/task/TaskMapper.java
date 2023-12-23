package com.github.straightth.mapper.task;

import com.github.straightth.domain.Task;
import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import com.github.straightth.mapper.user.UserMapperEnricher;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = UserMapperEnricher.class)
public interface TaskMapper {

    Task createTaskRequestToTask(CreateTaskRequest request);
    @Mapping(source = "assigneeUserId", target = "assigneeUser")
    TaskResponse taskToTaskResponse(Task task);
    Collection<TaskResponse> tasksToTaskResponses(Collection<Task> tasks);
}
