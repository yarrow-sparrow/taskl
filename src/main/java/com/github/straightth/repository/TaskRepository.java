package com.github.straightth.repository;

import com.github.straightth.domain.Task;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    Collection<Task> findAllByProjectId(String projectId);
}
