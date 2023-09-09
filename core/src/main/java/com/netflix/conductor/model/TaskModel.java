/*
 * Copyright 2022 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.netflix.conductor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.netflix.conductor.schema.metadata.tasks.Task;
import com.netflix.conductor.schema.metadata.tasks.TaskDef;
import com.netflix.conductor.schema.metadata.workflow.WorkflowTask;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class TaskModel {

    public enum Status {
        IN_PROGRESS(false, true, true),
        CANCELED(true, false, false),
        FAILED(true, false, true),
        FAILED_WITH_TERMINAL_ERROR(true, false, false),
        COMPLETED(true, true, true),
        COMPLETED_WITH_ERRORS(true, true, true),
        SCHEDULED(false, true, true),
        TIMED_OUT(true, false, true),
        SKIPPED(true, true, false);

        private final boolean terminal;

        private final boolean successful;

        private final boolean retriable;

        Status(boolean terminal, boolean successful, boolean retriable) {
            this.terminal = terminal;
            this.successful = successful;
            this.retriable = retriable;
        }

        public boolean isTerminal() {
            return terminal;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public boolean isRetriable() {
            return retriable;
        }
    }

    private String taskType;

    private Status status;

    private String referenceTaskName;

    private int retryCount;

    private int seq;

    private String correlationId;

    private int pollCount;

    private String taskDefName;

    /** Time when the task was scheduled */
    private long scheduledTime;

    /** Time when the task was first polled */
    private long startTime;

    /** Time when the task completed executing */
    private long endTime;

    /** Time when the task was last updated */
    private long updateTime;

    private int startDelayInSeconds;

    private String retriedTaskId;

    private boolean retried;

    private boolean executed;

    private boolean callbackFromWorker = true;

    private long responseTimeoutSeconds;

    private String workflowInstanceId;

    private String workflowType;

    private String taskId;

    private String reasonForIncompletion;

    private long callbackAfterSeconds;

    private String workerId;

    private WorkflowTask workflowTask;

    private String domain;

    private Object inputMessage;

    private Object outputMessage;

    private int rateLimitPerFrequency;

    private int rateLimitFrequencyInSeconds;

    private String externalInputPayloadStoragePath;

    private String externalOutputPayloadStoragePath;

    private int workflowPriority;

    private String executionNameSpace;

    private String isolationGroupId;

    private int iteration;

    private String subWorkflowId;

    // Timeout after which the wait task should be marked as completed
    private long waitTimeout;

    /**
     * Used to note that a sub workflow associated with SUB_WORKFLOW task has an action performed on
     * it directly.
     */
    private boolean subworkflowChanged;

    @JsonIgnore private Map<String, Object> inputPayload = new HashMap<>();

    @JsonIgnore private Map<String, Object> outputPayload = new HashMap<>();

    @JsonIgnore private Map<String, Object> inputData = new HashMap<>();

    @JsonIgnore private Map<String, Object> outputData = new HashMap<>();


    @JsonIgnore
    public Map<String, Object> getInputData() {
        if (!inputPayload.isEmpty() && !inputData.isEmpty()) {
            inputData.putAll(inputPayload);
            inputPayload = new HashMap<>();
            return inputData;
        } else if (inputPayload.isEmpty()) {
            return inputData;
        } else {
            return inputPayload;
        }
    }

    @JsonIgnore
    public void setInputData(Map<String, Object> inputData) {
        if (inputData == null) {
            inputData = new HashMap<>();
        }
        this.inputData = inputData;
    }

    @JsonIgnore
    public Map<String, Object> getOutputData() {
        if (!outputPayload.isEmpty() && !outputData.isEmpty()) {
            // Combine payload + data
            // data has precedence over payload because:
            //  with external storage enabled, payload contains the old values
            //  while data contains the latest and if payload took precedence, it
            //  would remove latest outputs
            outputPayload.forEach(outputData::putIfAbsent);
            outputPayload = new HashMap<>();
            return outputData;
        } else if (outputPayload.isEmpty()) {
            return outputData;
        } else {
            return outputPayload;
        }
    }

    @JsonIgnore
    public void setOutputData(Map<String, Object> outputData) {
        if (outputData == null) {
            outputData = new HashMap<>();
        }
        this.outputData = outputData;
    }

    public String getSubWorkflowId() {
        // For backwards compatibility
        if (StringUtils.isNotBlank(subWorkflowId)) {
            return subWorkflowId;
        } else {
            return this.getOutputData() != null && this.getOutputData().get("subWorkflowId") != null
                    ? (String) this.getOutputData().get("subWorkflowId")
                    : this.getInputData() != null
                            ? (String) this.getInputData().get("subWorkflowId")
                            : null;
        }
    }

    public void setSubWorkflowId(String subWorkflowId) {
        this.subWorkflowId = subWorkflowId;
        // For backwards compatibility
        if (this.outputData != null && this.outputData.containsKey("subWorkflowId")) {
            this.outputData.put("subWorkflowId", subWorkflowId);
        }
    }

    public void incrementPollCount() {
        ++this.pollCount;
    }

    /**
     * @return {@link Optional} containing the task definition if available
     */
    public Optional<TaskDef> getTaskDefinition() {
        return Optional.ofNullable(this.getWorkflowTask()).map(WorkflowTask::getTaskDefinition);
    }

    public boolean isLoopOverTask() {
        return iteration > 0;
    }

    /**
     * @return the queueWaitTime
     */
    public long getQueueWaitTime() {
        if (this.startTime > 0 && this.scheduledTime > 0) {
            if (this.updateTime > 0 && getCallbackAfterSeconds() > 0) {
                long waitTime =
                        System.currentTimeMillis()
                                - (this.updateTime + (getCallbackAfterSeconds() * 1000));
                return waitTime > 0 ? waitTime : 0;
            } else {
                return this.startTime - this.scheduledTime;
            }
        }
        return 0L;
    }

    /**
     * @return a copy of the task instance
     */
    public TaskModel copy() {
        TaskModel copy = new TaskModel();
        BeanUtils.copyProperties(this, copy);
        return copy;
    }

    public void externalizeInput(String path) {
        this.inputPayload = this.inputData;
        this.inputData = new HashMap<>();
        this.externalInputPayloadStoragePath = path;
    }

    public void externalizeOutput(String path) {
        this.outputPayload = this.outputData;
        this.outputData = new HashMap<>();
        this.externalOutputPayloadStoragePath = path;
    }

    public void internalizeInput(Map<String, Object> data) {
        this.inputData = new HashMap<>();
        this.inputPayload = data;
    }

    public void internalizeOutput(Map<String, Object> data) {
        this.outputData = new HashMap<>();
        this.outputPayload = data;
    }

    public Task toTask() {
        Task task = new Task();
        BeanUtils.copyProperties(this, task);
        task.setStatus(Task.Status.valueOf(status.name()));

        // ensure that input/output is properly represented
        if (externalInputPayloadStoragePath != null) {
            task.setInputData(new HashMap<>());
        }
        if (externalOutputPayloadStoragePath != null) {
            task.setOutputData(new HashMap<>());
        }
        return task;
    }

    public static Task.Status mapToTaskStatus(Status status) {
        return Task.Status.valueOf(status.name());
    }

    public void addInput(String key, Object value) {
        this.inputData.put(key, value);
    }

    public void addInput(Map<String, Object> inputData) {
        if (inputData != null) {
            this.inputData.putAll(inputData);
        }
    }

    public void addOutput(String key, Object value) {
        this.outputData.put(key, value);
    }

    public void addOutput(Map<String, Object> outputData) {
        if (outputData != null) {
            this.outputData.putAll(outputData);
        }
    }

    public void clearOutput() {
        this.outputData.clear();
        this.outputPayload.clear();
        this.externalOutputPayloadStoragePath = null;
    }
}
