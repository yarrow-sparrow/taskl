package com.github.yarrow.sparrow.repository;

import com.github.yarrow.sparrow.domain.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsUserByEmail(String email);
}
