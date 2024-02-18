package com.github.yarrow.sparrow.config;

import com.github.yarrow.sparrow.repository.ProjectRepository;
import com.github.yarrow.sparrow.repository.TaskRepository;
import com.github.yarrow.sparrow.repository.UserRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessServiceConfig {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
}
