package com.github.yarrow.sparrow.repository;

import com.github.yarrow.sparrow.domain.Project;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository<Project, String> {

    Collection<Project> findProjectsByMemberUserIdsContains(String userId);

    Set<Project> findProjectsByIdInAndMemberUserIdsContains(Collection<String> projectIds, String userId);
}
