package com.api.api.Repos;

import com.api.api.Entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
}