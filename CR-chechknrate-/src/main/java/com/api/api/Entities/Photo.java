package com.api.api.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Document(collection = "photos") // MongoDB koleksiyon adı
public class Photo {
    // Getters ve setters
    @Id
    private String id;
    private String name; // Fotoğraf adı
    private String type; // Fotoğraf türü (ör. image/jpeg)
    private byte[] data; // Fotoğrafın binary verisi
    private String userId;

    @DBRef
    @JsonIgnore
    private User owner;


    // Fotoğrafın aldığı puanlar
    private List<Rating> ratings;

    // Dinamik veya statik olarak saklanabilir
    private Double averageScore;

    public double calculateAverageScore() {
        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }
}