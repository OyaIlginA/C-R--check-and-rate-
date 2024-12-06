package com.api.api.Entities;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ratings")
@Data
public class Rating {

    private String userId;
    private int score;
}
