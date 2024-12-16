package com.api.api.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GridFsConfig {

    private final MongoClient mongoClient;

    // Constructor
    public GridFsConfig(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Bean
    public GridFSBucket gridFSBucket() {
        return GridFSBuckets.create(mongoClient.getDatabase("mongodb"));
    }


}
