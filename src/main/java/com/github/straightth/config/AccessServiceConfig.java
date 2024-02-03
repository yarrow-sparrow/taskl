package com.github.straightth.config;

import com.github.straightth.repository.ProjectRepository;
import com.github.straightth.repository.TaskRepository;
import com.github.straightth.repository.UserRepository;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessServiceConfig {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private TaskRepository taskRepository;
}
