package com.github.yarrow.sparrow.service.task;

import com.github.yarrow.sparrow.dto.request.CreateTaskRequest;
import com.github.yarrow.sparrow.dto.request.UpdateTaskRequest;
import com.github.yarrow.sparrow.dto.response.TaskResponse;
import com.github.yarrow.sparrow.exception.ErrorFactory;
import com.github.yarrow.sparrow.mapper.task.TaskMapper;
import com.github.yarrow.sparrow.repository.TaskRepository;
import com.github.yarrow.sparrow.service.project.ProjectAccessService;
import com.github.yarrow.sparrow.service.user.UserAccessService;
import com.github.yarrow.sparrow.util.SecurityUtil;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserAccessService userAccessService;
    private final ProjectAccessService projectAccessService;
    private final TaskAccessService taskAccessService;

    @Transactional
    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        //Security: ensuring that project is accessible by user
        projectAccessService.validatePresenceOrThrowSecured(request.getProjectId());

        var task = taskMapper.createTaskRequestToTask(request);
        if (request.getAssigneeUserId() == null) {
            var userId = SecurityUtil.getCurrentUserId();
            task.setAssigneeUserId(userId);
        }

        task = taskRepository.save(task);
        return taskMapper.taskToTaskResponse(task);
    }

    @Override
    public Collection<TaskResponse> getTasksByProjectId(String projectId) {
        //Security: ensuring that project is accessible by user
        projectAccessService.validatePresenceOrThrowSecured(projectId);

        var tasks = taskRepository.findAllByProjectId(projectId);
        return taskMapper.tasksToTaskResponses(tasks);
    }

    @Override
    public TaskResponse getTaskById(String taskId) {
        var task = taskAccessService.getPresentOrThrowSecured(taskId);
        return taskMapper.taskToTaskResponse(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTaskById(String taskId, UpdateTaskRequest request) {
        //noinspection DuplicatedCode: similar fields and update approach with Project, but different entities
        var task = taskAccessService.getPresentOrThrowSecured(taskId);

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
            userAccessService.validatePresenceOrThrow(assigneeUserId);
            task.setAssigneeUserId(assigneeUserId);
        }

        var storyPoints = request.getStoryPoints();
        if (storyPoints != null) {
            task.setStoryPoints(storyPoints);
        }

        var nullifyAssignee = request.getNullifyAssigneeUserId();
        if (nullifyAssignee != null) {
            if (StringUtils.isNotBlank(assigneeUserId)) {
                throw ErrorFactory.get().assigneeIdIsNotBlankOnNullify();
            }
            if (nullifyAssignee) {
                task.setAssigneeUserId(null);
            }
        }

        task = taskRepository.save(task);
        return taskMapper.taskToTaskResponse(task);
    }
}
