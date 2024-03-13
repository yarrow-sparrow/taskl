package com.github.yarrow.sparrow.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.yarrow.sparrow.MockMvcAbstractTest;
import com.github.yarrow.sparrow.authentication.WithUserMock;
import com.github.yarrow.sparrow.domain.Project;
import com.github.yarrow.sparrow.dto.request.CreateProjectRequest;
import com.github.yarrow.sparrow.dto.request.UpdateProjectRequest;
import com.github.yarrow.sparrow.repository.ProjectRepository;
import com.github.yarrow.sparrow.repository.UserRepository;
import com.github.yarrow.sparrow.util.TestEntityFactory;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class ProjectControllerTest extends MockMvcAbstractTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Nested
    class CreateProject {

        @Test
        @WithUserMock
        public void projectIsCreated() throws Exception {
            //Arrange
            var mockedUserId = getMockedUserId();

            var request = CreateProjectRequest.builder()
                    .name("Create project name")
                    .key("CREATE")
                    .description("Create project description")
                    .build();

            var clockTs = getClockTsMillis();
            var expectedProject = Project.builder()
                    .name("Create project name")
                    .key("CREATE")
                    .version(0L)
                    .createdTs(clockTs)
                    .updatedTs(clockTs)
                    .description("Create project description")
                    .memberUserIds(List.of(mockedUserId))
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Create project name"))
                    .andExpect(jsonPath("$.key").value("CREATE"))
                    //Mocked user is added to the project as a first member
                    .andExpect(jsonPath("$.memberUsers", hasSize(1)));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedProject);
        }

        @Test
        @WithUserMock
        public void keyIsCapitalized() throws Exception {
            //Arrange
            var request = defaultCreateProjectRequestBuilder()
                    .key("key")
                    .build();

            //Act
            var result = mockMvc.perform(post("/v1/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value("KEY"));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .extracting(Project::getKey)
                    .isEqualTo("KEY");
        }

        @Nested
        class Validation {

            @Nested
            class Key {

                @Test
                @WithUserMock
                public void nullInKeyLeadsTo400() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .key(null)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project key must be present"));
                }

                @Test
                @WithUserMock
                public void emptyKeyLeadsTo400() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .key("")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project key must be between 2 and 20 characters"));
                }

                @Test
                @WithUserMock
                public void twoCharacterNameLeadsTo200() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .name("AB")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void keyEqualToLimitLeadsTo200() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .key(StringUtils.repeat("A", 20))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isOk());
                }

                @Test
                @WithUserMock
                public void keyLongerThanLimitLeadsTo400() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .key(StringUtils.repeat("A", 21))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project key must be between 2 and 20 characters"));
                }

                @Test
                @WithUserMock
                public void invalidKeyLeadsTo400() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .key("INVALID1")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project key must consist only of A-Z characters"));
                }
            }

            @Nested
            class Name {

                @Test
                @WithUserMock
                public void nullInNameLeadsTo400() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .name(null)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name must be present"));
                }

                @Test
                @WithUserMock
                public void emptyNameLeadsTo400() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .name("")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
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
                    var request = defaultCreateProjectRequestBuilder()
                            .name("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
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
                    var request = defaultCreateProjectRequestBuilder()
                            .name(StringUtils.repeat("*", 30))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
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
                    var request = defaultCreateProjectRequestBuilder()
                            .name(StringUtils.repeat("*", 31))
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
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
                public void nullInDescriptionLeadsTo400() throws Exception {
                    //Arrange
                    var request = defaultCreateProjectRequestBuilder()
                            .description(null)
                            .build();

                    //Act
                    var result = mockMvc.perform(post("/v1/projects")
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

    @Nested
    class GetUserProjects {

        @Test
        @WithUserMock
        public void getProjects() throws Exception {
            //Arrange
            saveProject(p -> p.setName("Project 1"));
            saveProject(p -> p.setName("Project 2"));

            var otherUserId = saveOtherUser();
            saveProject(p -> {
                p.setName("Another user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            //Act
            var result = mockMvc.perform(get("/v1/projects").contentType(MediaType.APPLICATION_JSON));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value("Project 1"))
                    .andExpect(jsonPath("$[1].name").value("Project 2"));
        }

        @Test
        @WithUserMock
        public void noProjectsReturnsEmptyCollection() throws Exception {
            //Act
            var result = mockMvc.perform(get("/v1/projects").contentType(MediaType.APPLICATION_JSON));

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
            var expectedProjectId = saveProject(p -> p.setName("Expected project"));
            saveProject(p -> p.setName("Another project"));

            //Act
            var result = mockMvc.perform(get("/v1/projects/{projectId}", expectedProjectId));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedProjectId))
                    .andExpect(jsonPath("$.name").value("Expected project"));
        }

        @Test
        @WithUserMock
        public void getProjectByKey() throws Exception {
            //Arrange
            var expectedProjectId = saveProject(p -> {
                p.setKey("EXPECTED");
                p.setName("Expected project");
            });
            saveProject(p -> {
                p.setKey("ANOTHER");
                p.setName("Another project");
            });

            //Act
            var result = mockMvc.perform(get("/v1/projects/{projectId}", "EXPECTED"));

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedProjectId))
                    .andExpect(jsonPath("$.name").value("Expected project"));
        }

        @Test
        @WithUserMock
        public void nonexistentProjectLeadsTo404() throws Exception {
            //Arrange
            saveProject(p -> p.setName("Project 1"));

            //Act
            var result = mockMvc.perform(get("/v1/projects/{projectId}", RANDOM_UUID));

            //Assert
            result.andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Project not found"));
        }

        @Test
        @WithUserMock
        public void otherUserProjectInaccessibleForGet() throws Exception {
            //Arrange
            var otherUserId = saveOtherUser();
            var otherUserProjectId = saveProject(p -> {
                p.setName("Other user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            //Act
            var result = mockMvc.perform(get("/v1/projects/{projectId}", otherUserProjectId));

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
            var clockTs = getClockTsMillis();
            var mockedUserId = getMockedUserId();
            var projectId = saveProject(p -> {
                p.setKey("UPDATE");
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var otherUserId = saveOtherUser();
            var request = UpdateProjectRequest.builder()
                    .name("Update project name")
                    .description("Update project description")
                    .memberUserIds(List.of(mockedUserId, otherUserId))
                    .build();

            var expectedProject = Project.builder()
                    .id(projectId)
                    .key("UPDATE")
                    .version(1L)
                    .createdTs(clockTs)
                    .updatedTs(clockTs)
                    .name("Update project name")
                    .description("Update project description")
                    .memberUserIds(List.of(mockedUserId, otherUserId))
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/projects/{projectId}", projectId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Update project name"))
                    .andExpect(jsonPath("$.description").value("Update project description"))
                    .andExpect(jsonPath("$.memberUsers", hasSize(2)));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedProject);
        }

        @Test
        @WithUserMock
        public void projectIsUpdatedByKey() throws Exception {
            //Arrange
            var clockTs = getClockTsMillis();
            var mockedUserId = getMockedUserId();
            var projectId = saveProject(p -> {
                p.setKey("UPDATE");
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var otherUserId = saveOtherUser();
            var request = UpdateProjectRequest.builder()
                    .name("Update project name")
                    .description("Update project description")
                    .memberUserIds(List.of(mockedUserId, otherUserId))
                    .build();

            var expectedProject = Project.builder()
                    .id(projectId)
                    .key("UPDATE")
                    .version(1L)
                    .createdTs(clockTs)
                    .updatedTs(clockTs)
                    .name("Update project name")
                    .description("Update project description")
                    .memberUserIds(List.of(mockedUserId, otherUserId))
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/projects/{projectId}", "UPDATE")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            );

            //Assert
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Update project name"))
                    .andExpect(jsonPath("$.description").value("Update project description"))
                    .andExpect(jsonPath("$.memberUsers", hasSize(2)));

            var actualProject = projectRepository.findAll().getFirst();
            Assertions.assertThat(actualProject)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .ignoringCollectionOrder()
                    .isEqualTo(expectedProject);
        }

        @Test
        @WithUserMock
        public void emptyUpdateMakeNoChanges() throws Exception {
            //Arrange
            var clockTs = getClockTsMillis();
            var mockedUserId = getMockedUserId();
            var projectId = saveProject(p -> {
                p.setKey("UPDATE");
                p.setName("Initial name");
                p.setDescription("Initial description");
            });

            var expectedProject = Project.builder()
                    .id(projectId)
                    .key("UPDATE")
                    .version(1L)
                    .createdTs(clockTs)
                    .updatedTs(clockTs)
                    .name("Initial name")
                    .description("Initial description")
                    .memberUserIds(List.of(mockedUserId))
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/projects/{projectId}", projectId)
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
            saveProject(p -> p.setName("Project 1"));
            saveProject(p -> p.setName("Project 2"));

            var request = UpdateProjectRequest.builder().build();

            //Act
            var result = mockMvc.perform(put("/v1/projects/{projectId}", RANDOM_UUID)
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
            var projectId = saveProject(p -> p.setName("Initial name"));

            var request = UpdateProjectRequest.builder()
                    .memberUserIds(List.of(RANDOM_UUID))
                    .build();

            //Act
            var result = mockMvc.perform(put("/v1/projects/{projectId}", projectId)
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
            var otherUserId = saveOtherUser();
            var otherUserProjectId = saveProject(p -> {
                p.setName("Other user's project");
                p.setMemberUserIds(List.of(otherUserId));
            });

            //Act
            var result = mockMvc.perform(put("/v1/projects/{projectId}", otherUserProjectId));

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
                    var projectId = saveProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name("")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/projects/{projectId}", projectId)
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
                    var projectId = saveProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name("*")
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/projects/{projectId}", projectId)
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
                    var projectId = saveProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name(StringUtils.repeat("*", 30))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/projects/{projectId}", projectId)
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
                    var projectId = saveProject(p -> {
                        p.setName("Initial name");
                        p.setDescription("Initial description");
                    });

                    var request = UpdateProjectRequest.builder()
                            .name(StringUtils.repeat("*", 31))
                            .build();

                    //Act
                    var result = mockMvc.perform(put("/v1/projects/{projectId}", projectId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    );

                    //Assert
                    result.andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.message")
                                    .value("Project name must be between 1 and 30 characters"));
                }
            }
        }
    }

    private CreateProjectRequest.CreateProjectRequestBuilder defaultCreateProjectRequestBuilder() {
        return CreateProjectRequest.builder()
                .name("Create project name")
                .key("CREATE")
                .description("Create project description");
    }

    private String saveProject(Consumer<Project> preconfigure) {
        var project = TestEntityFactory.createProject();
        preconfigure.accept(project);
        return projectRepository.save(project).getId();
    }

    private String saveOtherUser() {
        var otherUser = TestEntityFactory.createUser();
        return userRepository.save(otherUser).getId();
    }
}
