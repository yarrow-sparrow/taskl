package com.github.straightth.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithUserMock;
import com.github.straightth.domain.Project;
import com.github.straightth.domain.User;
import com.github.straightth.dto.request.CreateProjectRequest;
import com.github.straightth.dto.request.UpdateProjectRequest;
import com.github.straightth.repository.ProjectRepository;
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

//TODO: test cases
public class ProjectControllerTest extends MockMvcAbstractTest {

    private static final String RANDOM_UUID = UUID.randomUUID().toString();

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Nested
    class CreateProject {

        @Test
        @WithUserMock
        public void createProject() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var otherUserId = createOtherUser();

            var request = CreateProjectRequest.builder()
                    .name("Created project name")
                    .description("Created project description")
                    .memberUserIds(List.of(otherUserId))
                    .build();

            var expectedProject = Project.builder()
                    .name("Created project name")
                    .description("Created project description")
                    .memberUserIds(List.of(mockedUserId, otherUserId))
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/project")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Created project name"))
                    .andExpect(jsonPath("$.description").value("Created project description"))
                    // Mocked user is added to the project as a first member
                    .andExpect(jsonPath("$.memberUsers", hasSize(2)));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "memberUserIds")
                    .isEqualTo(expectedProject);
            Assertions.assertThat(actualProject.getMemberUserIds())
                    .containsExactlyInAnyOrder(mockedUserId, otherUserId);
        }

        @Test
        @WithUserMock
        public void defaultParametersAreApplied() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();

            var expectedProject = Project.builder()
                    .name("New project")
                    .description("You can fill your description here")
                    .memberUserIds(List.of(mockedUserId))
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/project")
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New project"))
                    .andExpect(jsonPath("$.description").value("You can fill your description here"))
                    .andExpect(jsonPath("$.memberUsers[0].id").value(mockedUserId));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedProject);
        }

        @Test
        @WithUserMock
        public void nonexistentUserLeadsTo404() throws Exception {
            //Arrange
            var request = CreateProjectRequest.builder()
                    .memberUserIds(List.of(RANDOM_UUID))
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/project")
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
                    var request = CreateProjectRequest.builder()
                            .name("")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name must be between 1 and 30 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterNameLeadsTo200() throws Exception {
                    //Arrange
                    var request = CreateProjectRequest.builder()
                            .name("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
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
                    var request = CreateProjectRequest.builder()
                            .name(StringUtils.repeat("*", 30))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
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
                    var request = CreateProjectRequest.builder()
                            .name(StringUtils.repeat("*", 31))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name must be between 1 and 30 characters"));
                }

                @Test
                @WithUserMock
                public void nullInNameLeadsTo400() throws Exception {
                    //Arrange
                    @SuppressWarnings("DataFlowIssue")
                    var request = CreateProjectRequest.builder()
                            .name(null)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
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
                    var request = CreateProjectRequest.builder()
                            .description("")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message").value("Project description must be between 1 and 300 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterDescriptionLeadsTo200() throws Exception {
                    //Arrange
                    var request = CreateProjectRequest.builder()
                            .description("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
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
                    var request = CreateProjectRequest.builder()
                            .description(StringUtils.repeat("*", 300))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
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
                    var request = CreateProjectRequest.builder()
                            .description(StringUtils.repeat("*", 301))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project description must be between 1 and 300 characters"));
                }
            }

            @Test
            @WithUserMock
            public void nullInDescriptionLeadsTo400() throws Exception {
                //Arrange
                @SuppressWarnings("DataFlowIssue")
                var request = CreateProjectRequest.builder()
                        .description(null)
                        .build();

                //Act
                var result = mockMvc.perform(post("/v1/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                );

                //Assert
                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("must not be null"));
            }
        }
    }

    @Nested
    class GetUserProjects {

        @Test
        @WithUserMock
        public void getProjects() throws Exception {
            //Arrange
            createProject(p -> p.setName("Project 1"));
            createProject(p -> p.setName("Project 2"));

            var otherUserId = createOtherUser();
            createProject(p -> {
                p.setName("Another user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            //Act
            var result = mockMvc.perform(get("/v1/project").contentType(MediaType.APPLICATION_JSON));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Project 1"))
                    .andExpect(jsonPath("$[1].name").value("Project 2"));
        }

        @Test
        @WithUserMock
        public void noProjectsReturnsEmptyCollection() throws Exception {
            //Act
            var result = mockMvc.perform(get("/v1/project").contentType(MediaType.APPLICATION_JSON));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class GetUserProjectById {

        @Test
        @WithUserMock
        public void getProjectById() throws Exception {
            //Arrange
            var expectedProjectId = createProject(p -> p.setName("Expected project"));
            createProject(p -> p.setName("Another project"));

            //Act
            var result = mockMvc.perform(get("/v1/project/{projectId}", expectedProjectId));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedProjectId))
                    .andExpect(jsonPath("$.name").value("Expected project"));
        }

        @Test
        @WithUserMock
        public void nonexistentProjectLeadsTo404() throws Exception {
            //Arrange
            createProject(p -> p.setName("Project 1"));
            createProject(p -> p.setName("Project 2"));

            //Act
            var result = mockMvc.perform(get("/v1/project/{projectId}", RANDOM_UUID));

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }

        @Test
        @WithUserMock
        public void otherUserProjectInaccessibleForGet() throws Exception {
            //Arrange
            var otherUserId = createOtherUser();
            var otherUserProjectId = createProject(p -> {
                p.setName("Other user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            //Act
            var result = mockMvc.perform(get("/v1/project/{projectId}", otherUserProjectId));

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }
    }

    @Nested
    class UpdateProjectById {

        @Test
        @WithUserMock
        public void projectIsUpdated() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var projectId = createProject(p -> {
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var otherUserId = createOtherUser();

            var request = UpdateProjectRequest.builder()
                    .name("New name")
                    .description("New description")
                    .memberUserIds(List.of(mockedUserId, otherUserId))
                    .build();

            var expectedProject = Project.builder()
                    .id(projectId)
                    .name("New name")
                    .description("New description")
                    .memberUserIds(List.of(mockedUserId, otherUserId))
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New name"))
                    .andExpect(jsonPath("$.description").value("New description"))
                    .andExpect(jsonPath("$.memberUsers", hasSize(2)));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedProject);

            Assertions.assertThat(actualProject.getMemberUserIds())
                    .containsExactlyInAnyOrder(mockedUserId, otherUserId);
        }

        @Test
        @WithUserMock
        public void emptyUpdateMakeNoChanges() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();
            var projectId = createProject(p -> {
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var expectedProject = Project.builder()
                    .id(projectId)
                    .name("Initial name")
                    .description("Initial description")
                    .memberUserIds(List.of(mockedUserId))
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            //Assert
            result.andExpect(status().isOk());

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .isEqualTo(expectedProject);
        }

        @Test
        @WithUserMock
        public void nonexistentProjectLeadsTo404() throws Exception {
            //Arrange
            createProject(p -> p.setName("Project 1"));
            createProject(p -> p.setName("Project 2"));

            var request = UpdateProjectRequest.builder().build();

            //Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", RANDOM_UUID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Project not found"));
        }

        @Test
        @WithUserMock
        public void nonexistentUserLeadsTo404() throws Exception {
            //Arrange
            var projectId = createProject(p -> p.setName("Initial name"));

            var request = UpdateProjectRequest.builder()
                    .memberUserIds(List.of(RANDOM_UUID))
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("User not found"));
        }

        @Test
        @WithUserMock
        public void otherUserProjectInaccessibleForUpdate() throws Exception {
            //Arrange
            var otherUserId = createOtherUser();
            var otherUserProjectId = createProject(p -> {
                p.setName("Other user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            //Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", otherUserProjectId));

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Project not found"));
        }

        @Nested
        class Validation {

            @Nested
            class Name {

                @Test
                @WithUserMock
                public void emptyNameLeadsTo400() throws Exception {
                    //Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name("")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name must be between 1 and 30 characters"));
                }

                @Test
                @WithUserMock
                public void singleCharacterNameLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
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
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name(StringUtils.repeat("*", 30))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
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
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name(StringUtils.repeat("*", 31))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name must be between 1 and 30 characters"));
                }
            }

            @Nested
            class Description {

                @Test
                @WithUserMock
                public void singleCharacterDescriptionLeadsTo200() throws Exception {
                    //Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .description("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
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
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .description(StringUtils.repeat("*", 300))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
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
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .description(StringUtils.repeat("*", 301))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project description must be between 1 and 300 characters"));
                }
            }
        }
    }

    private String createProject(Consumer<Project> preconfigure) {
        var project = Project.builder()
                .name("Test name")
                .description("Test description")
                .memberUserIds(List.of(getMockedUserId()))
                .build();
        preconfigure.accept(project);
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
