package com.api.api.Repos;

import com.api.api.Entities.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PhotoRepo extends MongoRepository<Photo, String> {

  List<Photo> findByUser(String userId);

}
