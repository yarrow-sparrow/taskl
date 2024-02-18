package com.github.yarrow.sparrow.util;

import com.github.yarrow.sparrow.domain.Project;
import com.github.yarrow.sparrow.domain.Task;
import com.github.yarrow.sparrow.domain.TaskStatus;
import com.github.yarrow.sparrow.domain.User;
import java.util.List;
import lombok.experimental.UtilityClass;

/**
 * Utility class for encapsulation of default-value logic in tests
 */
@UtilityClass
public class TestEntityFactory {

    public static User createUser() {
        return User.builder()
                .username("user")
                .email("user@email.com")
                .password("P@ssw0rd")
                .build();
    }

    public static Project createProject() {
        return Project.builder()
                .name("Test name")
                .description("Test description")
                .memberUserIds(List.of(SecurityUtil.getCurrentUserId()))
                .build();
    }

    public static Task createTask() {
        return Task.builder()
                .projectId(null)
                .name("Test name")
                .description("Test description")
                .assigneeUserId(SecurityUtil.getCurrentUserId())
                .status(TaskStatus.BACKLOG)
                .storyPoints(1d)
                .build();
    }
}
