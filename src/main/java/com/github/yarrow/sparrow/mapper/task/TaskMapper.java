package com.github.yarrow.sparrow.mapper.task;

import com.github.yarrow.sparrow.domain.Task;
import com.github.yarrow.sparrow.dto.request.CreateTaskRequest;
import com.github.yarrow.sparrow.dto.response.TaskResponse;
import com.github.yarrow.sparrow.mapper.user.UserMapperEnricher;
import java.util.Collection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", uses = UserMapperEnricher.class)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    Task createTaskRequestToTask(CreateTaskRequest request);

    @Mapping(source = "assigneeUserId", target = "assigneeUser")
    TaskResponse taskToTaskResponse(Task task);

    Collection<TaskResponse> tasksToTaskResponses(Collection<Task> tasks);
}
