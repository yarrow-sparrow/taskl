package com.github.straightth.service.task;

import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.request.UpdateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import java.util.Collection;

public interface TaskService {

    TaskResponse createTask(String projectId, CreateTaskRequest request);
    Collection<TaskResponse> getProjectTasks(String projectId);
    TaskResponse getTaskById(String projectId, String taskId);
    TaskResponse updateTaskById(String projectId, String taskId, UpdateTaskRequest request);
}
