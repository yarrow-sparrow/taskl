package com.github.straightth.service.task;

import com.github.straightth.domain.Task;
import com.github.straightth.exception.task.TaskNotFound;
import com.github.straightth.service.access.AbstractAccessService;

public abstract class TaskAccessService extends AbstractAccessService<Task, String, TaskNotFound> {

    //TODO: we're considering our data consistent
    /**
     * Invalidating task's project id while finding it
     * @param projectId projectId to invalidate
     * @param taskId taskId to seek by
     * @return task
     */
    public Task getPresentCheckingProjectOrThrow(String projectId, String taskId) {
        var task = getPresentOrThrow(taskId);
        if (!task.getProjectId().equals(projectId)) {
            throw new TaskNotFound();
        }
        return task;
    }

    public void validatePresenceCheckingProjectOrThrow(String projectId, String taskId) {
        getPresentCheckingProjectOrThrow(projectId, taskId);
    }
}
