package com.github.straightth.util;

import com.github.straightth.domain.Project;
import com.github.straightth.domain.Task;
import com.github.straightth.domain.TaskStatus;
import com.github.straightth.domain.User;
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
