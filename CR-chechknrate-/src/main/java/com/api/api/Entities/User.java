package com.api.api.Entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
public class User {
    @Id
    String id;
    String username;
    String password;
    String apikey;

    private Double averageScore;

    @DBRef
    private List<Photo> photos = new ArrayList<>();

    public double calculateUserAverageScore() {
        return photos.stream()
                .mapToDouble(Photo::getAverageScore)
                .average()
                .orElse(0.0);
    }
}