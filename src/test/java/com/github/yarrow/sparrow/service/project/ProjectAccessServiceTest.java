package com.github.yarrow.sparrow.service.project;

import com.github.yarrow.sparrow.MockMvcAbstractTest;
import com.github.yarrow.sparrow.authentication.WithUserMock;
import com.github.yarrow.sparrow.domain.Project;
import com.github.yarrow.sparrow.exception.ApplicationError;
import com.github.yarrow.sparrow.repository.ProjectRepository;
import com.github.yarrow.sparrow.util.TestEntityFactory;
import java.util.List;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProjectAccessServiceTest extends MockMvcAbstractTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectAccessService projectAccessService;

    @Test
    @WithUserMock
    public void projectIsReturned() {
        //Arrange
        var expectedProjectId = saveProject(p -> {
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(getMockedUserId()));
        });

        var clockTs = getClockTsMillis();
        var expectedProject = Project.builder()
                .id(expectedProjectId)
                .key("KEY")
                .version(0L)
                .createdTs(clockTs)
                .updatedTs(clockTs)
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
    public void projectIsReturnedByKey() {
        //Arrange
        var expectedProjectId = saveProject(p -> {
            p.setKey("KEY");
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(getMockedUserId()));
        });

        var clockTs = getClockTsMillis();
        var expectedProject = Project.builder()
                .id(expectedProjectId)
                .key("KEY")
                .version(0L)
                .createdTs(clockTs)
                .updatedTs(clockTs)
                .name("Expected project name")
                .description("Expected project description")
                .memberUserIds(List.of(getMockedUserId()))
                .build();

        //Act
        var actualProject = projectAccessService.getPresentOrThrow("KEY");

        //Arrange
        Assertions.assertThat(actualProject)
                .isEqualTo(expectedProject);
    }

    @Test
    @WithUserMock
    public void securedProjectIsReturned() {
        //Arrange
        var expectedProjectId = saveProject(p -> {
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(getMockedUserId()));
        });

        var clockTs = getClockTsMillis();
        var expectedProject = Project.builder()
                .id(expectedProjectId)
                .key("KEY")
                .version(0L)
                .createdTs(clockTs)
                .updatedTs(clockTs)
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
    public void securedProjectIsReturnedByKey() {
        //Arrange
        var expectedProjectId = saveProject(p -> {
            p.setKey("KEY");
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(getMockedUserId()));
        });

        var clockTs = getClockTsMillis();
        var expectedProject = Project.builder()
                .id(expectedProjectId)
                .key("KEY")
                .version(0L)
                .createdTs(clockTs)
                .updatedTs(clockTs)
                .name("Expected project name")
                .description("Expected project description")
                .memberUserIds(List.of(getMockedUserId()))
                .build();

        //Act
        var actualProject = projectAccessService.getPresentOrThrowSecured("KEY");

        //Arrange
        Assertions.assertThat(actualProject)
                .isEqualTo(expectedProject);
    }

    @Test
    @WithUserMock
    public void inaccessibleSecuredProjectLeadsToThrow() {
        //Arrange
        var inaccessibleProjectId = saveProject(p -> {
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(RANDOM_UUID));
        });

        //Act + Assert
        Assertions.assertThatThrownBy(() -> projectAccessService.getPresentOrThrowSecured(inaccessibleProjectId))
                        .isExactlyInstanceOf(ApplicationError.class);
    }

    @Test
    @WithUserMock
    public void inaccessibleByKeySecuredProjectLeadsToThrow() {
        //Arrange
        saveProject(p -> {
            p.setKey("KEY");
            p.setName("Expected project name");
            p.setDescription("Expected project description");
            p.setMemberUserIds(List.of(RANDOM_UUID));
        });

        //Act + Assert
        Assertions.assertThatThrownBy(() -> projectAccessService.getPresentOrThrowSecured("KEY"))
                .isExactlyInstanceOf(ApplicationError.class);
    }

    private String saveProject(Consumer<Project> preconfigure) {
        var project = TestEntityFactory.createProject();
        preconfigure.accept(project);
        return projectRepository.save(project).getId();
    }
}
