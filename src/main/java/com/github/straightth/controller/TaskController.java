package com.github.straightth.controller;

import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.request.UpdateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import com.github.straightth.service.task.TaskService;
import jakarta.validation.Valid;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO:
 *  - move to root
 *  - make inaccessible for access other user's data
 */
@RestController
@RequestMapping("/v1/project/{projectId}/task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskResponse createTask(
            @PathVariable String projectId,
            @RequestBody @Valid CreateTaskRequest task
    ) {
        return taskService.createTask(projectId, task);
    }

    @GetMapping
    public Collection<TaskResponse> getTasksByProjectId(@PathVariable String projectId) {
        return taskService.getProjectTasks(projectId);
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTaskById(@PathVariable String projectId, @PathVariable String taskId) {
        return taskService.getTaskById(projectId, taskId);
    }

    @PutMapping("/{taskId}")
    public TaskResponse updateTaskById(
            @PathVariable String projectId,
            @PathVariable String taskId,
            @RequestBody @Valid UpdateTaskRequest request
    ) {
        return taskService.updateTaskById(projectId, taskId, request);
    }
}
