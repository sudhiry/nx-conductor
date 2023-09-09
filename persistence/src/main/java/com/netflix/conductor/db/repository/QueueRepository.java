package com.netflix.conductor.db.repository;

import com.netflix.conductor.db.models.QueueDocument;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QueueRepository extends MongoRepository<QueueDocument, String> {

    @ExistsQuery("{ name: ?0 }")
    boolean existsByName(String queueName);

}
