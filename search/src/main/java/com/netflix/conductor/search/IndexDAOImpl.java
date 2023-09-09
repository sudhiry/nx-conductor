package com.netflix.conductor.search;

import com.netflix.conductor.core.events.queue.Message;
import com.netflix.conductor.dao.IndexDAO;
import com.netflix.conductor.schema.metadata.events.EventExecution;
import com.netflix.conductor.schema.metadata.tasks.TaskExecLog;
import com.netflix.conductor.schema.run.SearchResult;
import com.netflix.conductor.schema.run.TaskSummary;
import com.netflix.conductor.schema.run.WorkflowSummary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class IndexDAOImpl implements IndexDAO {

    @Override
    public void setup() throws Exception {

    }

    @Override
    public void indexWorkflow(WorkflowSummary workflow) {

    }

    @Override
    public CompletableFuture<Void> asyncIndexWorkflow(WorkflowSummary workflow) {
        return null;
    }

    @Override
    public void indexTask(TaskSummary task) {

    }

    @Override
    public CompletableFuture<Void> asyncIndexTask(TaskSummary task) {
        return null;
    }

    @Override
    public SearchResult<String> searchWorkflows(String query, String freeText, int start, int count, List<String> sort) {
        return null;
    }

    @Override
    public SearchResult<WorkflowSummary> searchWorkflowSummary(String query, String freeText, int start, int count, List<String> sort) {
        return null;
    }

    @Override
    public SearchResult<String> searchTasks(String query, String freeText, int start, int count, List<String> sort) {
        return null;
    }

    @Override
    public SearchResult<TaskSummary> searchTaskSummary(String query, String freeText, int start, int count, List<String> sort) {
        return null;
    }

    @Override
    public void removeWorkflow(String workflowId) {

    }

    @Override
    public CompletableFuture<Void> asyncRemoveWorkflow(String workflowId) {
        return null;
    }

    @Override
    public void updateWorkflow(String workflowInstanceId, String[] keys, Object[] values) {

    }

    @Override
    public CompletableFuture<Void> asyncUpdateWorkflow(String workflowInstanceId, String[] keys, Object[] values) {
        return null;
    }

    @Override
    public void removeTask(String workflowId, String taskId) {

    }

    @Override
    public CompletableFuture<Void> asyncRemoveTask(String workflowId, String taskId) {
        return null;
    }

    @Override
    public void updateTask(String workflowId, String taskId, String[] keys, Object[] values) {

    }

    @Override
    public CompletableFuture<Void> asyncUpdateTask(String workflowId, String taskId, String[] keys, Object[] values) {
        return null;
    }

    @Override
    public String get(String workflowInstanceId, String key) {
        return null;
    }

    @Override
    public void addTaskExecutionLogs(List<TaskExecLog> logs) {

    }

    @Override
    public CompletableFuture<Void> asyncAddTaskExecutionLogs(List<TaskExecLog> logs) {
        return null;
    }

    @Override
    public List<TaskExecLog> getTaskExecutionLogs(String taskId) {
        return null;
    }

    @Override
    public void addEventExecution(EventExecution eventExecution) {

    }

    @Override
    public List<EventExecution> getEventExecutions(String event) {
        return null;
    }

    @Override
    public CompletableFuture<Void> asyncAddEventExecution(EventExecution eventExecution) {
        return null;
    }

    @Override
    public void addMessage(String queue, Message msg) {

    }

    @Override
    public CompletableFuture<Void> asyncAddMessage(String queue, Message message) {
        return null;
    }

    @Override
    public List<Message> getMessages(String queue) {
        return null;
    }

    @Override
    public List<String> searchArchivableWorkflows(String indexName, long archiveTtlDays) {
        return null;
    }

    @Override
    public long getWorkflowCount(String query, String freeText) {
        return 0;
    }
}
