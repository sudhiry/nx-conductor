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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.netflix.conductor.schema.metadata.workflow.WorkflowDef;
import com.netflix.conductor.schema.run.Workflow;
import com.netflix.conductor.core.utils.Utils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class WorkflowModel {

    public enum Status {
        RUNNING(false, false),
        COMPLETED(true, true),
        FAILED(true, false),
        TIMED_OUT(true, false),
        TERMINATED(true, false),
        PAUSED(false, true);

        private final boolean terminal;
        private final boolean successful;

        Status(boolean terminal, boolean successful) {
            this.terminal = terminal;
            this.successful = successful;
        }

        public boolean isTerminal() {
            return terminal;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    private Status status = Status.RUNNING;

    private long endTime;

    private String workflowId;

    private String parentWorkflowId;

    private String parentWorkflowTaskId;

    private List<TaskModel> tasks = new LinkedList<>();

    private String correlationId;

    private String reRunFromWorkflowId;

    private String reasonForIncompletion;

    private String event;

    private Map<String, String> taskToDomain = new HashMap<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> failedReferenceTaskNames = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> failedTaskNames = new HashSet<>();

    private WorkflowDef workflowDefinition;

    private int priority;

    private Map<String, Object> variables = new HashMap<>();

    private long lastRetriedTime;

    private String ownerApp;

    private Long createTime;

    private Long updatedTime;

    private String createdBy;

    private String updatedBy;

    // Capture the failed taskId if the workflow execution failed because of task failure
    private String failedTaskId;

    private Status previousStatus;

    @JsonProperty("input")
    private Map<String, Object> input = new HashMap<>();

    @JsonProperty("output")
    private Map<String, Object> output = new HashMap<>();

    public void setStatus(Status status) {
        // update previous status if current status changed
        if (this.status != status) {
            setPreviousStatus(this.status);
        }
        this.status = status;
    }

    @JsonIgnore
    public void setInput(Map<String, Object> input) {
        if (input == null) {
            input = new HashMap<>();
        }
        this.input = input;
    }

    @JsonIgnore
    public void setOutput(Map<String, Object> output) {
        if (output == null) {
            output = new HashMap<>();
        }
        this.output = output;
    }

    public void setPriority(int priority) {
        if (priority < 0 || priority > 99) {
            throw new IllegalArgumentException("priority MUST be between 0 and 99 (inclusive)");
        }
        this.priority = priority;
    }

    /**
     * Convenience method for accessing the workflow definition name.
     *
     * @return the workflow definition name.
     */
    public String getWorkflowName() {
        Utils.checkNotNull(workflowDefinition, "Workflow definition is null");
        return workflowDefinition.getName();
    }

    /**
     * Convenience method for accessing the workflow definition version.
     *
     * @return the workflow definition version.
     */
    public int getWorkflowVersion() {
        Utils.checkNotNull(workflowDefinition, "Workflow definition is null");
        return workflowDefinition.getVersion();
    }

    public boolean hasParent() {
        return StringUtils.isNotEmpty(parentWorkflowId);
    }

    /**
     * A string representation of all relevant fields that identify this workflow. Intended for use
     * in log and other system generated messages.
     */
    public String toShortString() {
        String name = workflowDefinition != null ? workflowDefinition.getName() : null;
        Integer version = workflowDefinition != null ? workflowDefinition.getVersion() : null;
        return String.format("%s.%s/%s", name, version, workflowId);
    }

    public TaskModel getTaskByRefName(String refName) {
        if (refName == null) {
            throw new RuntimeException(
                    "refName passed is null.  Check the workflow execution.  For dynamic tasks, make sure referenceTaskName is set to a not null value");
        }
        LinkedList<TaskModel> found = new LinkedList<>();
        for (TaskModel task : tasks) {
            if (task.getReferenceTaskName() == null) {
                throw new RuntimeException(
                        "Task "
                                + task.getTaskDefName()
                                + ", seq="
                                + task.getSeq()
                                + " does not have reference name specified.");
            }
            if (task.getReferenceTaskName().equals(refName)) {
                found.add(task);
            }
        }
        if (found.isEmpty()) {
            return null;
        }
        return found.getLast();
    }

    @Override
    public String toString() {
        String name = workflowDefinition != null ? workflowDefinition.getName() : null;
        Integer version = workflowDefinition != null ? workflowDefinition.getVersion() : null;
        return String.format("%s.%s/%s.%s", name, version, workflowId, status);
    }

    public Workflow toWorkflow() {
        Workflow workflow = new Workflow();
        BeanUtils.copyProperties(this, workflow);
        workflow.setStatus(Workflow.WorkflowStatus.valueOf(this.status.name()));
        workflow.setTasks(tasks.stream().map(TaskModel::toTask).collect(Collectors.toList()));
        workflow.setUpdateTime(this.updatedTime);
        return workflow;
    }

    public void addInput(String key, Object value) {
        this.input.put(key, value);
    }

    public void addInput(Map<String, Object> inputData) {
        if (inputData != null) {
            this.input.putAll(inputData);
        }
    }

    public void addOutput(String key, Object value) {
        this.output.put(key, value);
    }

    public void addOutput(Map<String, Object> outputData) {
        if (outputData != null) {
            this.output.putAll(outputData);
        }
    }
}
