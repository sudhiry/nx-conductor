package com.netflix.conductor.db.repository;

import com.netflix.conductor.db.models.TaskScheduled;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.ExistsQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskScheduledRepository extends MongoRepository<TaskScheduled, String> {

    @ExistsQuery("{ workflow_id: ?0, task_key: ?1 }")
    boolean existsTaskScheduledByWorkflowIdAndTaskKey(String workflowId, String taskKey);

    @DeleteQuery("{ workflow_id: ?0, task_key: ?1 }")
    void removeTaskScheduledByWorkflowIdAndTaskKey(String workflowId, String taskKey);
}
