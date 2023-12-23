package com.github.straightth.mapper.task;

import com.github.straightth.domain.Task;
import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.response.TaskResponse;
import com.github.straightth.mapper.user.UserMapperEnricher;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-23T18:58:50+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (BellSoft)"
)
@Component
public class TaskMapperImpl implements TaskMapper {

    @Autowired
    private UserMapperEnricher userMapperEnricher;

    @Override
    public Task createTaskRequestToTask(CreateTaskRequest request) {
        if ( request == null ) {
            return null;
        }

        Task.TaskBuilder task = Task.builder();

        task.name( request.getName() );
        task.description( request.getDescription() );
        task.assigneeUserId( request.getAssigneeUserId() );
        task.status( request.getStatus() );
        task.storyPoints( request.getStoryPoints() );
        task.deadline( request.getDeadline() );

        return task.build();
    }

    @Override
    public TaskResponse taskToTaskResponse(Task task) {
        if ( task == null ) {
            return null;
        }

        TaskResponse.TaskResponseBuilder taskResponse = TaskResponse.builder();

        taskResponse.assigneeUser( userMapperEnricher.userToUserShortResponse( task.getAssigneeUserId() ) );
        taskResponse.id( task.getId() );
        taskResponse.projectId( task.getProjectId() );
        taskResponse.name( task.getName() );
        taskResponse.description( task.getDescription() );
        taskResponse.status( task.getStatus() );
        taskResponse.storyPoints( task.getStoryPoints() );
        taskResponse.daysLeft( task.getDaysLeft() );

        return taskResponse.build();
    }

    @Override
    public Collection<TaskResponse> tasksToTaskResponses(Collection<Task> tasks) {
        if ( tasks == null ) {
            return null;
        }

        Collection<TaskResponse> collection = new ArrayList<TaskResponse>( tasks.size() );
        for ( Task task : tasks ) {
            collection.add( taskToTaskResponse( task ) );
        }

        return collection;
    }
}
