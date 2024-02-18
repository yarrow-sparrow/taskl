package com.github.yarrow.sparrow.repository;

import com.github.yarrow.sparrow.domain.Task;
import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    Collection<Task> findAllByProjectId(String projectId);
}
