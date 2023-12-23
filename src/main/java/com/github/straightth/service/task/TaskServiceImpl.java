package com.github.straightth.service.task;

import com.github.straightth.mapper.task.TaskMapper;
import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.request.UpdateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import com.github.straightth.repository.TaskRepository;
import com.github.straightth.service.user.UserAccessService;
import com.github.straightth.util.SecurityUtil;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO:
 *  - enrich task with assignee
 *  - test task API
 */
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserAccessService userPresenceService;
    private final TaskAccessService taskPresenceService;

    @Transactional
    @Override
    public TaskResponse createTask(String projectId, CreateTaskRequest request) {
        var task = taskMapper.createTaskRequestToTask(request);
        var userId = SecurityUtil.getCurrentUserId();
        task.setAssigneeUserId(userId);
        task.setProjectId(projectId);
        task = taskRepository.save(task);
        return taskMapper.taskToTaskResponse(task);
    }

    @Override
    public Collection<TaskResponse> getProjectTasks(String projectId) {
        var tasks = taskRepository.findAllByProjectId(projectId);
        return taskMapper.tasksToTaskResponses(tasks);
    }

    @Override
    public TaskResponse getTaskById(String projectId, String taskId) {
        var task = taskPresenceService.getPresentCheckingProjectOrThrow(projectId, taskId);
        return taskMapper.taskToTaskResponse(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTaskById(String projectId, String taskId, UpdateTaskRequest request) {
        var task = taskPresenceService.getPresentCheckingProjectOrThrow(projectId, taskId);

        var name = request.getName();
        if (StringUtils.isNotBlank(name)) {
            task.setName(name);
        }
        var description = request.getDescription();
        if (StringUtils.isNotBlank(description)) {
            task.setDescription(description);
        }
        var status = request.getStatus();
        if (status != null) {
            task.setStatus(status);
        }
        var assigneeUserId = request.getAssigneeUserId();
        if (assigneeUserId != null) {
            userPresenceService.validatePresenceOrThrow(assigneeUserId);
            task.setAssigneeUserId(assigneeUserId);
        }
        var storyPoints = request.getStoryPoints();
        if (storyPoints != null) {
            task.setStoryPoints(storyPoints);
        }
        var deadline = request.getDeadline();
        if (deadline != null) {
            task.setDeadline(deadline);
        }

        task = taskRepository.save(task);

        return taskMapper.taskToTaskResponse(task);
    }
}
