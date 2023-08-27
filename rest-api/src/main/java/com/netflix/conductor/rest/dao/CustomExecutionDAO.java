package com.netflix.conductor.rest.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.netflix.conductor.common.metadata.tasks.TaskType;
import com.netflix.conductor.model.TaskModel;
import com.netflix.conductor.mongo.db.dao.MongoDBBaseDAO;
import com.netflix.conductor.mongo.db.models.TaskDocument;
import com.netflix.conductor.mongo.db.models.TaskInProgress;
import com.netflix.conductor.mongo.db.repository.TaskInProgressRepository;
import com.netflix.conductor.mongo.db.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CustomExecutionDAO  extends MongoDBBaseDAO {

    public final TaskInProgressRepository taskInProgressRepository;

    public final TaskRepository taskRepository;

    public CustomExecutionDAO(ObjectMapper objectMapper,
                              @Qualifier("MongoDBRetryTemplate") RetryTemplate retryTemplate,
                              TaskInProgressRepository taskInProgressRepository,
                              TaskRepository taskRepository) {
        super(objectMapper, retryTemplate);
        this.taskInProgressRepository = taskInProgressRepository;
        this.taskRepository = taskRepository;
    }

    public List<TaskModel> getPendingTasksByTaskType(TaskType taskType, String workflowId) {
        Preconditions.checkNotNull(workflowId, "workflowId cannot be null or empty");
        Preconditions.checkNotNull(taskType, "taskType cannot be null or empty");
        List<TaskInProgress> taskInProgresses = taskInProgressRepository.getTaskInProgressesByTaskTypeAndWorkflowId(taskType.toString(), workflowId);
        return convertTaskInProgressToTaskModel(taskInProgresses);
    }
    private List<TaskModel> convertTaskInProgressToTaskModel(List<TaskInProgress> taskInProgresses) {
        if(taskInProgresses == null || taskInProgresses.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> taskIds = taskInProgresses.stream().map(TaskInProgress::getTaskId).toList();
        List<TaskDocument> taskDocuments = taskRepository.getTasksByTaskId(taskIds);
        return taskDocuments.stream().map(taskDocument -> readValue(taskDocument.getJsonData(), TaskModel.class)).toList();
    }
}
