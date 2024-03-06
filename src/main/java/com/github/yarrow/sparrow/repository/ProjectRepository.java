package com.github.yarrow.sparrow.repository;

import com.github.yarrow.sparrow.domain.Project;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {

    Collection<Project> findAllByMemberUserIdsContains(String userId);

    Set<Project> findAllByIdInAndMemberUserIdsContains(Collection<String> projectIds, String userId);

    Collection<Project> findAllByKeyIn(Collection<String> projectKeys);

    Collection<Project> findAllByKeyInAndMemberUserIdsContains(Collection<String> projectKeys, String userId);
}
