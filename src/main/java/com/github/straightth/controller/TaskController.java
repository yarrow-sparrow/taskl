package com.github.straightth.controller;

import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.request.UpdateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import com.github.straightth.service.task.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/task")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public TaskResponse createTask(@RequestBody @Valid CreateTaskRequest task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public Collection<TaskResponse> getTasksByProjectId(@RequestParam @Valid @NotNull String projectId) {
        return taskService.getTasksByProjectId(projectId);
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTaskById(@PathVariable String taskId) {
        return taskService.getTaskById(taskId);
    }

    @PutMapping("/{taskId}")
    public TaskResponse updateTaskById(@PathVariable String taskId, @RequestBody @Valid UpdateTaskRequest request) {
        return taskService.updateTaskById(taskId, request);
    }
}
