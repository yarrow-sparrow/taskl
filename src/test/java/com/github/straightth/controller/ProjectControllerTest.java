package com.github.straightth.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithMockCustomUser;
import com.github.straightth.domain.Project;
import com.github.straightth.domain.User;
import com.github.straightth.dto.request.CreateProjectRequest;
import com.github.straightth.dto.request.UpdateProjectRequest;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.repository.UserRepository;
import com.github.straightth.util.Constants;
import com.github.straightth.util.SecurityUtil;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

//TODO: refactor it after error factory
public class ProjectControllerTest extends MockMvcAbstractTest {

    private static final String RANDOM_UUID = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    class CreateProject {

        @Test
        @WithMockCustomUser
        public void createProject() throws Exception {
            // Arrange
            var mockUserId = SecurityUtil.getCurrentUserId();
            var otherUserId = createOtherUser();

            var request = CreateProjectRequest.builder()
                    .name("New name")
                    .description("New description")
                    .memberUserIds(List.of(otherUserId))
                    .build();

            var expectedProject = Project.builder()
                    .name("New name")
                    .description("New description")
                    .memberUserIds(List.of(mockUserId, otherUserId))
                    .build();

            // Act
            var result = mockMvc.perform(post("/v1/project")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New name"))
                    .andExpect(jsonPath("$.description").value("New description"))
                    // Mocked user is added to the project as a first member
                    .andExpect(jsonPath("$.memberUsers", hasSize(2)));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id", "memberUserIds")
                    .isEqualTo(expectedProject);
            Assertions.assertThat(actualProject.getMemberUserIds())
                    .containsExactlyInAnyOrder(mockUserId, otherUserId);
        }

        @Test
        @WithMockCustomUser
        public void defaultParametersAreApplied() throws Exception {
            // Arrange
            var mockUserId = SecurityUtil.getCurrentUserId();

            var expectedProject = Project.builder()
                    .name(Constants.Project.DEFAULT_NAME)
                    .description(Constants.Project.DEFAULT_DESCRIPTION)
                    .memberUserIds(List.of(mockUserId))
                    .build();

            // Act
            var result = mockMvc.perform(post("/v1/project")
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(Constants.Project.DEFAULT_NAME))
                    .andExpect(jsonPath("$.description").value(Constants.Project.DEFAULT_DESCRIPTION))
                    .andExpect(jsonPath("$.memberUsers[0].id").value(mockUserId));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(expectedProject);
        }

        @Test
        @WithMockCustomUser
        public void nonexistentUserLeadsTo404() throws Exception {
            // Arrange
            var request = CreateProjectRequest.builder()
                    .memberUserIds(List.of(RANDOM_UUID))
                    .build();

            // Act
            var result = mockMvc.perform(post("/v1/project")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("User not found"));
        }

        @Nested
        class Validation {

            @Nested
            class Name {

                @Test
                @WithMockCustomUser
                public void nullInNameLeadsTo400() throws Exception {
                    // Arrange
                    @SuppressWarnings("DataFlowIssue")
                    var request = CreateProjectRequest.builder()
                            .name(null)
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            //TODO validation message
                            .andExpect(jsonPath("$.message").value("must not be null"));
                }

                @Test
                @WithMockCustomUser
                public void emptyNameLeadsTo400() throws Exception {
                    // Arrange
                    var request = CreateProjectRequest.builder()
                            .name("")
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name should be between 1 and 30 characters"));
                }

                @Test
                @WithMockCustomUser
                public void singleCharacterNameLeadsTo400() throws Exception {
                    // Arrange
                    var request = CreateProjectRequest.builder()
                            .name("*")
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithMockCustomUser
                public void nameEqualToLimitLeadsTo200() throws Exception {
                    // Arrange
                    var request = CreateProjectRequest.builder()
                            .name(StringUtils.repeat("*", Constants.Project.NAME_MAX_LENGTH + 1))
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name should be between 1 and 30 characters"));
                }

                @Test
                @WithMockCustomUser
                public void nameLongerThanLimitLeadsTo400() throws Exception {
                    // Arrange
                    var request = CreateProjectRequest.builder()
                            .name(StringUtils.repeat("*", Constants.Project.NAME_MAX_LENGTH + 1))
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name should be between 1 and 30 characters"));
                }
            }

            @Nested
            class Description {

                @Test
                @WithMockCustomUser
                public void nullInDescriptionLeadsTo400() throws Exception {
                    // Arrange
                    @SuppressWarnings("DataFlowIssue")
                    var request = CreateProjectRequest.builder()
                            .description(null)
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            //TODO validation message
                            .andExpect(jsonPath("$.message").value("must not be null"));
                }

                @Test
                @WithMockCustomUser
                public void singleCharacterDescriptionLeadsToOk() throws Exception {
                    // Arrange
                    var request = CreateProjectRequest.builder()
                            .description("*")
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithMockCustomUser
                public void descriptionEqualToLimitLeadsTo200() throws Exception {
                    // Arrange
                    var request = CreateProjectRequest.builder()
                            .description(StringUtils.repeat("*", Constants.Project.DESCRIPTION_MAX_LENGTH))
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithMockCustomUser
                public void descriptionLongerThanLimitLeadsTo400() throws Exception {
                    // Arrange
                    var request = CreateProjectRequest.builder()
                            .description(StringUtils.repeat("*", Constants.Project.DESCRIPTION_MAX_LENGTH + 1))
                            .build();

                    // Act
                    var result = mockMvc.perform(post("/v1/project")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project description should be between 1 and 300 characters"));
                }
            }
        }
    }

    @Nested
    class GetUserProjects {

        @Test
        @WithMockCustomUser
        public void getProjects() throws Exception {
            // Arrange
            createProject(p -> p.setName("Project 1"));
            createProject(p -> p.setName("Project 2"));

            var otherUserId = createOtherUser();
            createProject(p -> {
                p.setName("Another user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            // Act
            var result = mockMvc.perform(get("/v1/project").contentType(MediaType.APPLICATION_JSON));

            // Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Project 1"))
                    .andExpect(jsonPath("$[1].name").value("Project 2"));
        }

        @Test
        @WithMockCustomUser
        public void noProjectsReturnsEmptyCollection() throws Exception {
            // Act
            var result = mockMvc.perform(get("/v1/project").contentType(MediaType.APPLICATION_JSON));

            // Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    class GetUserProjectById {

        @Test
        @WithMockCustomUser
        public void getProjectById() throws Exception {
            // Arrange
            var expectedProjectId = createProject(p -> p.setName("Expected project"));
            createProject(p -> p.setName("Another project"));

            // Act
            var result = mockMvc.perform(get("/v1/project/{projectId}", expectedProjectId));

            // Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedProjectId))
                    .andExpect(jsonPath("$.name").value("Expected project"));
        }

        @Test
        @WithMockCustomUser
        public void nonexistentProjectLeadsTo404() throws Exception {
            // Arrange
            createProject(p -> p.setName("Project 1"));
            createProject(p -> p.setName("Project 2"));

            // Act
            var result = mockMvc.perform(get("/v1/project/{projectId}", RANDOM_UUID));

            // Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }

        @Test
        @WithMockCustomUser
        public void otherUserProjectInaccessibleForGet() throws Exception {
            // Arrange
            var otherUserId = createOtherUser();
            var otherUserProjectId = createProject(p -> {
                p.setName("Other user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            // Act
            var result = mockMvc.perform(get("/v1/project/{projectId}", otherUserProjectId));

            // Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }
    }

    @Nested
    class UpdateProjectById {

        @Test
        @WithMockCustomUser
        public void updatedEntityIsBeingReturned() throws Exception {
            // Arrange
            var mockUserId = SecurityUtil.getCurrentUserId();
            var projectId = createProject(p -> {
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var otherUserId = createOtherUser();

            var request = UpdateProjectRequest.builder()
                    .name("New name")
                    .description("New description")
                    .memberUserIds(List.of(mockUserId, otherUserId))
                    .build();

            var expectedProject = Project.builder()
                    .id(projectId)
                    .name("New name")
                    .description("New description")
                    .memberUserIds(List.of(mockUserId, otherUserId))
                    .build();

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
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
                    .containsExactlyInAnyOrder(mockUserId, otherUserId);
        }

        @Test
        @WithMockCustomUser
        public void emptyUpdateIsNotApplied() throws Exception {
            // Arrange
            var mockUserId = SecurityUtil.getCurrentUserId();
            var projectId = createProject(p -> {
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var expectedProject = Project.builder()
                    .id(projectId)
                    .name("Initial name")
                    .description("Initial description")
                    .memberUserIds(List.of(mockUserId))
                    .build();

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
            );

            // Assert
            result.andExpect(status().isOk());

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .isEqualTo(expectedProject);
        }

        @Test
        @WithMockCustomUser
        public void alterProjectName() throws Exception {
            // Arrange
            var projectId = createProject(p -> {
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var request = UpdateProjectRequest.builder().name("New name").build();

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New name"));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject.getName()).isEqualTo("New name");
        }

        @Test
        @WithMockCustomUser
        public void alterProjectDescription() throws Exception {
            // Arrange
            var projectId = createProject(p -> {
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var request = UpdateProjectRequest.builder().description("New description").build();

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.description").value("New description"));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject.getDescription()).isEqualTo("New description");
        }

        @Test
        @WithMockCustomUser
        public void alterProjectMembers() throws Exception {
            // Arrange
            var mockUserId = getCurrentUserId();
            var projectId = createProject(p -> {
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var otherUserId = createOtherUser();

            var request = UpdateProjectRequest.builder()
                    .memberUserIds(List.of(mockUserId, otherUserId))
                    .build();

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
            result.andExpect(status().isOk());

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject.getMemberUserIds())
                    .containsExactlyInAnyOrder(mockUserId, otherUserId);
        }

        @Test
        @WithMockCustomUser
        public void nonexistentProjectLeadsTo404() throws Exception {
            // Arrange
            createProject(p -> p.setName("Project 1"));
            createProject(p -> p.setName("Project 2"));

            var request = UpdateProjectRequest.builder().build();

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", RANDOM_UUID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Project not found"));
        }

        @Test
        @WithMockCustomUser
        public void nonexistentUserLeadsTo404() throws Exception {
            // Arrange
            var projectId = createProject(p -> p.setName("Initial name"));

            var request = UpdateProjectRequest.builder()
                    .memberUserIds(List.of(RANDOM_UUID))
                    .build();

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            // Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("User not found"));
        }

        @Test
        @WithMockCustomUser
        public void otherUserProjectInaccessibleForUpdate() throws Exception {
            // Arrange
            var otherUserId = createOtherUser();
            var otherUserProjectId = createProject(p -> {
                p.setName("Other user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            // Act
            var result = mockMvc.perform(put("/v1/project/{projectId}", otherUserProjectId));

            // Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Project not found"));
        }

        @Nested
        class Validation {

            @Nested
            class Name {

                @Test
                @WithMockCustomUser
                public void emptyNameLeadsTo400() throws Exception {
                    // Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name("")
                            .build();

                    // Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name should be between 1 and 30 characters"));
                }

                @Test
                @WithMockCustomUser
                public void singleCharacterNameLeadsTo200() throws Exception {
                    // Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name("*")
                            .build();

                    // Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithMockCustomUser
                public void nameEqualToLimitLeadsTo200() throws Exception {
                    // Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name(StringUtils.repeat("*", Constants.Project.NAME_MAX_LENGTH + 1))
                            .build();

                    // Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name should be between 1 and 30 characters"));
                }

                @Test
                @WithMockCustomUser
                public void nameLongerThanLimitLeadsTo400() throws Exception {
                    // Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name(StringUtils.repeat("*", Constants.Project.NAME_MAX_LENGTH + 1))
                            .build();

                    // Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name should be between 1 and 30 characters"));
                }
            }

            @Nested
            class Description {

                @Test
                @WithMockCustomUser
                public void singleCharacterDescriptionLeadsTo200() throws Exception {
                    // Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .description("*")
                            .build();

                    // Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithMockCustomUser
                public void descriptionEqualToLimitLeadsTo200() throws Exception {
                    // Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .description(StringUtils.repeat("*", Constants.Project.DESCRIPTION_MAX_LENGTH))
                            .build();

                    // Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithMockCustomUser
                public void descriptionLongerThanLimitLeadsTo400() throws Exception {
                    // Arrange
                    var projectId = createProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .description(StringUtils.repeat("*", Constants.Project.DESCRIPTION_MAX_LENGTH + 1))
                            .build();

                    // Act
                    var result = mockMvc.perform(put("/v1/project/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    // Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project description should be between 1 and 300 characters"));
                }
            }
        }
    }

    private String createProject(Consumer<Project> preconfigure) {
        var project = Project.builder()
                .name(Constants.Project.DEFAULT_NAME)
                .description(Constants.Project.DEFAULT_DESCRIPTION)
                .memberUserIds(List.of(getCurrentUserId()))
                .build();
        preconfigure.accept(project);
        project = projectRepository.save(project);
        return project.getId();
    }

    private String createOtherUser() {
        var anotherUser = User.builder()
                .username("user")
                .email("user@email.com")
                .password("password")
                .build();
        return userRepository.save(anotherUser).getId();
    }

    private String getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
