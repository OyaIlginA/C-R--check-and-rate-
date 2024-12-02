package com.api.api.Entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}