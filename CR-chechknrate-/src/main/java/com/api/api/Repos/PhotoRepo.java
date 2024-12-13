package com.api.api.Repos;

import com.api.api.Entities.Photo;
import com.api.api.Entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PhotoRepo extends MongoRepository<Photo, String> {

  List<Photo> findByUserId(String userId);

  List<Photo> findByOwner(User owner);
}
