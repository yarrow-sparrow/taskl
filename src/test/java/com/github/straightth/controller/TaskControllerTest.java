package com.github.straightth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithUserMock;
import com.github.straightth.domain.Project;
import com.github.straightth.domain.Task;
import com.github.straightth.domain.TaskStatus;
import com.github.straightth.domain.User;
import com.github.straightth.dto.request.CreateTaskRequest;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.repository.TaskRepository;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
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
        public void createTask() throws Exception {
            //Arrange
            var mockedUserId = SecurityUtil.getCurrentUserId();
            var projectId = createProject();

            var request = CreateTaskRequest.builder()
                    .projectId(projectId)
                    .name("New name")
                    .description("New description")
                    .assigneeUserId(mockedUserId)
                    .status(TaskStatus.BACKLOG)
                    .storyPoints(1d)
                    .build();

            var expectedTask = Task.builder()
                    .projectId(projectId)
                    .name("New name")
                    .description("New description")
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
                    .andExpect(jsonPath("$.name").value("New name"))
                    .andExpect(jsonPath("$.description").value("New description"))
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
            var mockedUserId = getCurrentUserId();
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
            var projectId = createProject();
            var mockedUserId = getCurrentUserId();

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
                                    .value("Task name should be between 1 and 30 characters"));
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
                                    .value("Task name should be between 1 and 30 characters"));
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

            @Ignore
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
                                    .value("Task description should be between 1 and 300 characters"));
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
                                    .value("Task description should be between 1 and 300 characters"));
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
        }
    }

    private String createProject() {
        var project = Project.builder()
                .name("Test name")
                .description("Test description")
                .memberUserIds(List.of(getCurrentUserId()))
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
