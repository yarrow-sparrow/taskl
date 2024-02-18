package com.github.yarrow.sparrow.service.task;

import com.github.yarrow.sparrow.dto.request.CreateTaskRequest;
import com.github.yarrow.sparrow.dto.request.UpdateTaskRequest;
import com.github.yarrow.sparrow.dto.response.TaskResponse;
import java.util.Collection;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    Collection<TaskResponse> getTasksByProjectId(String projectId);

    TaskResponse getTaskById(String taskId);

    TaskResponse updateTaskById(String taskId, UpdateTaskRequest request);
}
