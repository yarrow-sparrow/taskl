package com.github.straightth.repository;

import com.github.straightth.domain.User;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsUserByEmail(String email);
}
