package com.github.straightth.service.task;

import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.request.UpdateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import java.util.Collection;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    Collection<TaskResponse> getTasksByProjectId(String projectId);

    TaskResponse getTaskById(String taskId);

    TaskResponse updateTaskById(String taskId, UpdateTaskRequest request);
}
