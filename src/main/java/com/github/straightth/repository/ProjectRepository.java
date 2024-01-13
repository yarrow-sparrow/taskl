package com.github.straightth.repository;

import com.github.straightth.domain.Project;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {

    Collection<Project> findProjectsByMemberUserIdsContains(String userId);

    Optional<Project> findProjectByIdAndMemberUserIdsContains(String projectId, String userId);

    Set<Project> findProjectsByIdInAndMemberUserIdsContains(Collection<String> projectIds, String userId);
}
