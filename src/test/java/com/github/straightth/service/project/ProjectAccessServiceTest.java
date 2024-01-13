package com.github.straightth.service.project;

import com.github.straightth.MockMvcAbstractTest;
import com.github.straightth.authentication.WithUserMock;
import com.github.straightth.domain.Project;
import com.github.straightth.exception.ApplicationError;
import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.util.TestEntityFactory;
import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

public class ProjectAccessServiceTest extends MockMvcAbstractTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectAccessService projectAccessService;

    @Test
    @WithUserMock
    public void projectIsReturned() {
        //Arrange
        var expectedProjectId = createProject(p -> {
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(getMockedUserId()));
        });

        var expectedProject = Project.builder()
                .id(expectedProjectId)
                .name("Expected project name")
                .description("Expected project description")
                .memberUserIds(List.of(getMockedUserId()))
                .build();

        //Act
        var actualProject = projectAccessService.getPresentOrThrow(expectedProjectId);

        //Arrange
        Assertions.assertThat(actualProject)
                .isEqualTo(expectedProject);
    }

    @Test
    @WithUserMock
    public void securedProjectIsReturned() {
        //Arrange
        var expectedProjectId = createProject(p -> {
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(getMockedUserId()));
        });

        var expectedProject = Project.builder()
                .id(expectedProjectId)
                .name("Expected project name")
                .description("Expected project description")
                .memberUserIds(List.of(getMockedUserId()))
                .build();

        //Act
        var actualProject = projectAccessService.getPresentOrThrowSecured(expectedProjectId);

        //Arrange
        Assertions.assertThat(actualProject)
                .isEqualTo(expectedProject);
    }

    @Test
    @WithUserMock
    public void inaccessibleSecuredProjectLeadsToThrow() {
        //Arrange
        var inaccessibleProjectId = createProject(p -> {
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(RANDOM_UUID));
        });

        //Act + Assert
        Assertions.assertThatThrownBy(() -> projectAccessService.getPresentOrThrowSecured(inaccessibleProjectId))
                        .isExactlyInstanceOf(ApplicationError.class);
    }

    private String createProject(Consumer<Project> preconfigure) {
        var project = TestEntityFactory.createProject();
        preconfigure.accept(project);
        return projectRepository.save(project).getId();
    }
}
