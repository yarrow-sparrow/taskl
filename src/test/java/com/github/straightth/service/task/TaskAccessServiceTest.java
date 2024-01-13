package com.github.straightth.service.task;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithUserMock;
import com.github.straightth.domain.Task;
import com.github.straightth.domain.TaskStatus;
import com.github.straightth.exception.ApplicationError;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.repository.TaskRepository;
import com.github.straightth.util.TestEntityFactory;
import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskAccessServiceTest extends MockMvcAbstractTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAccessService taskAccessService;

    @Test
    @WithUserMock
    public void taskIsReturned() {
        //Arrange
        var projectId = createProject();

        var expectedTaskId = createTask(t -> {
            t.setProjectId(projectId);
            t.setName("Expected task");
            t.setDescription("Expected task description");
            t.setAssigneeUserId(getMockedUserId());
            t.setStatus(TaskStatus.REVIEW);
            t.setStoryPoints(1d);
        });

        var expectedTask = Task.builder()
                .id(expectedTaskId)
                .projectId(projectId)
                .name("Expected task")
                .description("Expected task description")
                .assigneeUserId(getMockedUserId())
                .status(TaskStatus.REVIEW)
                .storyPoints(1d)
                .build();

        //Act
        var actualTask = taskAccessService.getPresentOrThrow(expectedTaskId);

        //Assert
        Assertions.assertThat(actualTask).isEqualTo(expectedTask);
    }

    @Test
    @WithUserMock
    public void securedTaskIsReturned() {
        //Arrange
        var projectId = createProject();

        var expectedTaskId = createTask(t -> {
            t.setProjectId(projectId);
            t.setName("Expected task");
            t.setDescription("Expected task description");
            t.setAssigneeUserId(getMockedUserId());
            t.setStatus(TaskStatus.REVIEW);
            t.setStoryPoints(1d);
        });

        var expectedTask = Task.builder()
                .id(expectedTaskId)
                .projectId(projectId)
                .name("Expected task")
                .description("Expected task description")
                .assigneeUserId(getMockedUserId())
                .status(TaskStatus.REVIEW)
                .storyPoints(1d)
                .build();

        //Act
        var actualTask = taskAccessService.getPresentOrThrowSecured(expectedTaskId);

        //Assert
        Assertions.assertThat(actualTask).isEqualTo(expectedTask);
    }

    @Test
    @WithUserMock
    public void inaccessibleSecuredTaskLeadsToThrow() {
        var projectId = createInaccessibleProject();
        var inaccessibleTaskId = createTask(t -> t.setProjectId(projectId));

        //Act + Assert
        Assertions.assertThatThrownBy(() -> taskAccessService.getPresentOrThrowSecured(inaccessibleTaskId))
                .isExactlyInstanceOf(ApplicationError.class);
    }

    private String createProject() {
        var project = TestEntityFactory.createProject();
        return projectRepository.save(project).getId();
    }

    private String createInaccessibleProject() {
        var project = TestEntityFactory.createProject();
        project.setMemberUserIds(List.of(RANDOM_UUID));
        return projectRepository.save(project).getId();
    }

    private String createTask(Consumer<Task> preconfigure) {
        var task = TestEntityFactory.createTask();
        preconfigure.accept(task);
        return taskRepository.save(task).getId();
    }
}
