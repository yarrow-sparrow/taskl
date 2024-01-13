package com.github.straightth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithUserMock;
import com.github.straightth.domain.Project;
import com.github.straightth.domain.Task;
import com.github.straightth.domain.TaskStatus;
import com.github.straightth.domain.User;
import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.dto.request.UpdateTaskRequest;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.repository.TaskRepository;
import com.github.straightth.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class TaskControllerTest extends MockMvcAbstractTest {

    private static final String RANDOM_UUID = UUID.randomUUID().toString();

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Nested
    class CreateTask {

        @Test
        @WithUserMock
        public void taskIsCreated() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var projectId = createProject();

            var request = CreateTaskRequest.builder()
                    .projectId(projectId)
                    .name("Created task name")
                    .description("Created task description")
                    .assigneeUserId(mockedUserId)
                    .status(TaskStatus.BACKLOG)
                    .storyPoints(1d)
                    .build();

            var expectedTask = Task.builder()
                    .projectId(projectId)
                    .name("Created task name")
                    .description("Created task description")
                    .assigneeUserId(mockedUserId)
                    .status(TaskStatus.BACKLOG)
                    .storyPoints(1d)
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectId").value(projectId))
                    .andExpect(jsonPath("$.name").value("Created task name"))
                    .andExpect(jsonPath("$.description").value("Created task description"))
                    .andExpect(jsonPath("$.assigneeUser.id").value(mockedUserId))
                    .andExpect(jsonPath("$.status").value("BACKLOG"))
                    .andExpect(jsonPath("$.storyPoints").value(1d));

            var actualTask = taskRepository.findAll().getFirst();
            Assertions.assertThat(actualTask)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedTask);
        }

        @Test
        @WithUserMock
        public void defaultParametersAreApplied() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var projectId = createProject();

            var request = CreateTaskRequest.builder().projectId(projectId).build();

            var expectedTask = Task.builder()
                    .projectId(projectId)
                    .name("New task")
                    .description("You can fill your description here")
                    .assigneeUserId(mockedUserId)
                    .status(TaskStatus.BACKLOG)
                    .storyPoints(0d)
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectId").value(projectId))
                    .andExpect(jsonPath("$.name").value("New task"))
                    .andExpect(jsonPath("$.description").value("You can fill your description here"))
                    .andExpect(jsonPath("$.assigneeUser.id").value(mockedUserId))
                    .andExpect(jsonPath("$.status").value("BACKLOG"))
                    .andExpect(jsonPath("$.storyPoints").value(0d));

            var actualTask = taskRepository.findAll().getFirst();
            Assertions.assertThat(actualTask)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedTask);
        }

        @Test
        @WithUserMock
        public void taskCouldBeAssignedToUserHimself() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var projectId = createProject();

            var request = CreateTaskRequest.builder()
                    .projectId(projectId)
                    .assigneeUserId(mockedUserId)
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.assigneeUser.id").value(mockedUserId));

            var actualTask = taskRepository.findAll().getFirst();
            Assertions.assertThat(actualTask.getAssigneeUserId()).isEqualTo(mockedUserId);
        }

        @Test
        @WithUserMock
        public void taskCouldBeAssignedToOtherUser() throws Exception {
            //Arrange
            var projectId = createProject();
            var otherUserId = createOtherUser();

            var request = CreateTaskRequest.builder()
                    .projectId(projectId)
                    .assigneeUserId(otherUserId)
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.assigneeUser.id").value(otherUserId));

            var actualTask = taskRepository.findAll().getFirst();
            Assertions.assertThat(actualTask.getAssigneeUserId()).isEqualTo(otherUserId);
        }

        @Test
        @WithUserMock
        public void nonexistentProjectLeadsTo404() throws Exception {
            //Arrange
            var request = CreateTaskRequest.builder()
                    .projectId(RANDOM_UUID)
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }

        @Test
        @WithUserMock
        public void inaccessibleProjectLeadsTo404() throws Exception {
            //Arrange
            var inaccessibleProjectId = createInaccessibleProject();
            var request = CreateTaskRequest.builder()
                    .projectId(inaccessibleProjectId)
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }

        @Test
        @WithUserMock
        public void nullInProjectIdLeadsTo400() throws Exception {
            //Arrange
            @SuppressWarnings("DataFlowIssue")
            var request = CreateTaskRequest.builder()
                    .projectId(null)
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isBadRequest());
        }

        @Nested
        class Validation {

            @Nested
            class Name {

                @Test
                @WithUserMock
                public void emptyNameLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .name("")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task name must be between 1 and 30 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterNameLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .name("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void nameEqualToLimitLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .name(StringUtils.repeat("*", 30))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void nameLongerThanLimitLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .name(StringUtils.repeat("*", 31))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task name must be between 1 and 30 characters"));
                }

                @Test
                @WithUserMock
                public void nullInNameLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    @SuppressWarnings("DataFlowIssue")
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .name(null)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("must not be null"));
                }

            }

            @Nested
            class Description {

                @Test
                @WithUserMock
                public void emptyDescriptionLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .description("")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task description must be between 1 and 300 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterDescriptionLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .description("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void descriptionEqualToLimitLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .description(StringUtils.repeat("*", 300))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void descriptionLongerThanLimitLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .description(StringUtils.repeat("*", 301))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task description must be between 1 and 300 characters"));
                }

                @Test
                @WithUserMock
                public void nullInDescriptionLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    @SuppressWarnings("DataFlowIssue")
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .description(null)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("must not be null"));
                }
            }

            @Nested
            class StoryPoints {

                @Test
                @WithUserMock
                public void positiveStoryPointsLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .storyPoints(1d)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void zeroStoryPointsLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .storyPoints(0d)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void negativeStoryPointsLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var request = CreateTaskRequest.builder()
                            .projectId(projectId)
                            .storyPoints(-1d)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task story points must be positive or zero"));
                }
            }
        }
    }

    @Nested
    class GetTasksWithinProject {

        @Test
        @WithUserMock
        public void tasksIsReturned() throws Exception {
            //Arrange
            var projectId = createProject();

            createTask(t -> {
                t.setProjectId(projectId);
                t.setName("Task 1");
            });
            createTask(t -> {
                t.setProjectId(projectId);
                t.setName("Task 2");
            });

            //Act
            var result = mockMvc.perform(get("/v1/task").param("projectId", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Task 1"))
                    .andExpect(jsonPath("$[1].name").value("Task 2"));
        }

        @Test
        @WithUserMock
        public void noTasksLeadsToEmptyCollection() throws Exception {
            //Arrange
            var projectId = createProject();

            //Act
            var result = mockMvc.perform(get("/v1/task").param("projectId", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @WithUserMock
        public void nullInProjectIdLeadsTo400() throws Exception {
            //Act
            var result = mockMvc.perform(get("/v1/task")
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Bad request"));
        }

        @Test
        @WithUserMock
        public void nonexistentProjectLeadsTo404() throws Exception {
            //Act
            var result = mockMvc.perform(get("/v1/task").param("projectId", RANDOM_UUID)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }

        @Test
        @WithUserMock
        public void inaccessibleProjectLeadsTo404() throws Exception {
            //Arrange
            var projectId = createInaccessibleProject();

            //Act
            var result = mockMvc.perform(get("/v1/task").param("projectId", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }
    }

    @Nested
    class GetTaskById {

        @Test
        @WithUserMock
        public void taskIsReturned() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var projectId = createProject();

            var expectedTask = Task.builder()
                    .projectId(projectId)
                    .name("Task name")
                    .description("Task description")
                    .assigneeUserId(mockedUserId)
                    .status(TaskStatus.BACKLOG)
                    .storyPoints(1d)
                    .build();
            var task = taskRepository.save(expectedTask);

            //Act
            var result = mockMvc.perform(get("/v1/task/{taskId}", task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectId").value(projectId))
                    .andExpect(jsonPath("$.name").value("Task name"))
                    .andExpect(jsonPath("$.description").value("Task description"))
                    .andExpect(jsonPath("$.assigneeUser.id").value(mockedUserId))
                    .andExpect(jsonPath("$.status").value("BACKLOG"))
                    .andExpect(jsonPath("$.storyPoints").value(1d));
        }

        @Test
        @WithUserMock
        public void nonexistentTaskLeadsTo404() throws Exception {
            //Act
            var result = mockMvc.perform(get("/v1/task/{taskId}", RANDOM_UUID)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @WithUserMock
        public void inaccessibleProjectLeadsTo404() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var inaccessibleProjectId = createInaccessibleProject();

            var expectedTask = Task.builder()
                    .projectId(inaccessibleProjectId)
                    .name("Task name")
                    .description("Task description")
                    .assigneeUserId(mockedUserId)
                    .status(TaskStatus.BACKLOG)
                    .storyPoints(1d)
                    .build();
            var task = taskRepository.save(expectedTask);

            //Act
            var result = mockMvc.perform(get("/v1/task/{taskId}", task.getId())
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }
    }

    @Nested
    class UpdateTaskById {

        @Test
        @WithUserMock
        public void taskIsUpdated() throws Exception {
            //Arrange
            var projectId = createProject();
            var mockedUserId = getMockedUserId();
            var otherUserId = createOtherUser();

            var taskId = createTask(t -> {
                t.setProjectId(projectId);
                t.setName("Task to update");
                t.setAssigneeUserId(mockedUserId);
            });

            var request = UpdateTaskRequest.builder()
                    .name("Updated task name")
                    .description("Updated task description")
                    .assigneeUserId(otherUserId)
                    .status(TaskStatus.REVIEW)
                    .storyPoints(2.7d)
                    .build();

            var expectedTask = Task.builder()
                    .id(taskId)
                    .projectId(projectId)
                    .name("Updated task name")
                    .description("Updated task description")
                    .assigneeUserId(otherUserId)
                    .status(TaskStatus.REVIEW)
                    .storyPoints(2.7d)
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectId").value(projectId))
                    .andExpect(jsonPath("$.name").value("Updated task name"))
                    .andExpect(jsonPath("$.description").value("Updated task description"))
                    .andExpect(jsonPath("$.assigneeUser.id").value(otherUserId))
                    .andExpect(jsonPath("$.status").value("REVIEW"))
                    .andExpect(jsonPath("$.storyPoints").value(2.7d));

            var actualTask = taskRepository.findAll().getFirst();
            Assertions.assertThat(actualTask)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedTask);
        }

        @Test
        @WithUserMock
        public void emptyUpdateMakeNoChanges() throws Exception {
            //Arrange
            var projectId = createProject();
            var mockedUserId = getMockedUserId();

            var taskId = createTask(t -> {
                t.setProjectId(projectId);
                t.setName("Task name to keep");
                t.setDescription("Task description to keep");
                t.setAssigneeUserId(mockedUserId);
                t.setStatus(TaskStatus.IN_PROGRESS);
                t.setStoryPoints(3.1d);
            });

            var request = UpdateTaskRequest.builder().build();

            var expectedTask = Task.builder()
                    .id(taskId)
                    .projectId(projectId)
                    .name("Task name to keep")
                    .description("Task description to keep")
                    .assigneeUserId(mockedUserId)
                    .status(TaskStatus.IN_PROGRESS)
                    .storyPoints(3.1d)
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.projectId").value(projectId))
                    .andExpect(jsonPath("$.name").value("Task name to keep"))
                    .andExpect(jsonPath("$.description").value("Task description to keep"))
                    .andExpect(jsonPath("$.assigneeUser.id").value(mockedUserId))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                    .andExpect(jsonPath("$.storyPoints").value(3.1d));

            var actualTask = taskRepository.findAll().getFirst();
            Assertions.assertThat(actualTask)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedTask);
        }

        @Test
        @WithUserMock
        public void nonexistentTaskLeadsTo404() throws Exception {
            //Arrange
            var request = UpdateTaskRequest.builder().build();

            //Act
            var result = mockMvc.perform(put("/v1/task/{taskId}", RANDOM_UUID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @WithUserMock
        public void inaccessibleProjectLeadsTo404() throws Exception {
            //Arrange
            var inaccessibleProjectId = createInaccessibleProject();

            var taskId = createTask(t -> t.setProjectId(inaccessibleProjectId));

            var request = UpdateTaskRequest.builder().build();

            //Act
            var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Task not found"));
        }

        @Test
        @WithUserMock
        public void nonexistentAssigneeLeadsTo404() throws Exception {
            //Arrange
            var projectId = createProject();
            var mockedUserId = getMockedUserId();

            var taskId = createTask(t -> {
                t.setProjectId(projectId);
                t.setAssigneeUserId(mockedUserId);
            });

            var request = UpdateTaskRequest.builder()
                    .assigneeUserId(RANDOM_UUID)
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Nested
        class Validation {

            @Nested
            class Name {

                @Test
                @WithUserMock
                public void emptyNameLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .name("")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task name must be between 1 and 30 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterNameLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .name("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void nameEqualToLimitLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .name(StringUtils.repeat("*", 30))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void nameLongerThanLimitLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .name(StringUtils.repeat("*", 31))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task name must be between 1 and 30 characters"));
                }
            }

            @Nested
            class Description {

                @Test
                @WithUserMock
                public void emptyDescriptionLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .description("")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task description must be between 1 and 300 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterDescriptionLeadsTo200() throws Exception {
                    ///Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .description("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void descriptionEqualToLimitLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .description(StringUtils.repeat("*", 300))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void descriptionLongerThanLimitLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .description(StringUtils.repeat("*", 301))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task description must be between 1 and 300 characters"));
                }
            }

            @Nested
            class StoryPoints {

                @Test
                @WithUserMock
                public void positiveStoryPointsLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .storyPoints(1d)
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void zeroStoryPointsLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .storyPoints(0d)
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void negativeStoryPointsLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject();
                    var taskId = createTask(t -> t.setProjectId(projectId));

                    var request = UpdateTaskRequest.builder()
                            .storyPoints(-1d)
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/task/{taskId}", taskId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Task story points must be positive or zero"));
                }
            }
        }
    }

    private String createTask(Consumer<Task> preconfigure) {
        var task = Task.builder()
                .projectId(null)
                .name("Test name")
                .description("Test description")
                .assigneeUserId(getMockedUserId())
                .status(TaskStatus.BACKLOG)
                .storyPoints(1d)
                .build();
        preconfigure.accept(task);
        return taskRepository.save(task).getId();
    }

    private String createProject() {
        var project = Project.builder()
                .name("Test name")
                .description("Test description")
                .memberUserIds(List.of(getMockedUserId()))
                .build();
        return projectRepository.save(project).getId();
    }

    private String createInaccessibleProject() {
        var project = Project.builder()
                .name("Test name")
                .description("Test description")
                .memberUserIds(List.of(RANDOM_UUID))
                .build();
        return projectRepository.save(project).getId();
    }

    private String createOtherUser() {
        var anotherUser = User.builder()
                .username("user")
                .email("user@email.com")
                .password("password")
                .build();
        return userRepository.save(anotherUser).getId();
    }
}
