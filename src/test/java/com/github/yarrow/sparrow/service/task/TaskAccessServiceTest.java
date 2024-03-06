package com.github.yarrow.sparrow.service.task;

import com.github.yarrow.sparrow.MockMvcAbstractTest;
import com.github.yarrow.sparrow.authentication.WithUserMock;
import com.github.yarrow.sparrow.domain.Task;
import com.github.yarrow.sparrow.domain.TaskStatus;
import com.github.yarrow.sparrow.exception.ApplicationError;
import com.github.yarrow.sparrow.repository.ProjectRepository;
import com.github.yarrow.sparrow.repository.TaskRepository;
import com.github.yarrow.sparrow.util.TestEntityFactory;
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
        var projectId = saveProject();

        var expectedTaskId = saveTask(t -> {
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
        var projectId = saveProject();

        var expectedTaskId = saveTask(t -> {
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
        var inaccessibleTaskId = saveTask(t -> t.setProjectId(projectId));

        //Act + Assert
        Assertions.assertThatThrownBy(() -> taskAccessService.getPresentOrThrowSecured(inaccessibleTaskId))
                .isExactlyInstanceOf(ApplicationError.class);
    }

    private String saveProject() {
        var project = TestEntityFactory.createProject();
        return projectRepository.save(project).getId();
    }

    private String createInaccessibleProject() {
        var project = TestEntityFactory.createProject();
        project.setMemberUserIds(List.of(RANDOM_UUID));
        return projectRepository.save(project).getId();
    }

    private String saveTask(Consumer<Task> preconfigure) {
        var task = TestEntityFactory.createTask();
        preconfigure.accept(task);
        return taskRepository.save(task).getId();
    }
}
