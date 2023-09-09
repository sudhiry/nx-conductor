/*
 * Copyright 2020 Netflix, Inc.
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
package com.netflix.conductor.schema.metadata.workflow;

import com.netflix.conductor.schema.constraints.NoSemiColonConstraint;
import com.netflix.conductor.schema.constraints.OwnerEmailMandatoryConstraint;
import com.netflix.conductor.schema.constraints.TaskReferenceNameUniqueConstraint;
import com.netflix.conductor.schema.metadata.BaseDef;
import com.netflix.conductor.schema.metadata.tasks.TaskType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.*;

@Data
@TaskReferenceNameUniqueConstraint
public class WorkflowDef extends BaseDef {

    public enum TimeoutPolicy {
        TIME_OUT_WF,
        ALERT_ONLY
    }

    @NotEmpty(message = "WorkflowDef name cannot be null or empty")
    @NoSemiColonConstraint(
            message = "Workflow name cannot contain the following set of characters: ':'")
    private String name;

    private String description;

    private int version = 1;

    @NotNull
    @NotEmpty(message = "WorkflowTask list cannot be empty")
    private List<@Valid WorkflowTask> tasks = new LinkedList<>();

    private List<String> inputParameters = new LinkedList<>();

    private Map<String, Object> outputParameters = new HashMap<>();

    private String failureWorkflow;

    @Min(value = 2, message = "workflowDef schemaVersion: {value} is only supported")
    @Max(value = 2, message = "workflowDef schemaVersion: {value} is only supported")
    private int schemaVersion = 2;

    // By default, a workflow is restartable
    private boolean restartable = true;

    private boolean workflowStatusListenerEnabled = false;

    @OwnerEmailMandatoryConstraint
    @Email(message = "ownerEmail should be valid email address")
    private String ownerEmail;

    private TimeoutPolicy timeoutPolicy = TimeoutPolicy.ALERT_ONLY;

    @NotNull
    private long timeoutSeconds;

    private Map<String, Object> variables = new HashMap<>();

    private Map<String, Object> inputTemplate = new HashMap<>();

    public String key() {
        return getKey(name, version);
    }

    public static String getKey(String name, int version) {
        return name + "." + version;
    }

    public boolean containsType(String taskType) {
        return collectTasks().stream().anyMatch(t -> t.getType().equals(taskType));
    }

    public WorkflowTask getNextTask(String taskReferenceName) {
        WorkflowTask workflowTask = getTaskByRefName(taskReferenceName);
        if (workflowTask != null && TaskType.TERMINATE.name().equals(workflowTask.getType())) {
            return null;
        }

        Iterator<WorkflowTask> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            WorkflowTask task = iterator.next();
            if (task.getTaskReferenceName().equals(taskReferenceName)) {
                // If taskReferenceName matches, break out
                break;
            }
            WorkflowTask nextTask = task.next(taskReferenceName, null);
            if (nextTask != null) {
                return nextTask;
            } else if (TaskType.DO_WHILE.name().equals(task.getType())
                    && !task.getTaskReferenceName().equals(taskReferenceName)
                    && task.has(taskReferenceName)) {
                // If the task is child of Loop Task and at last position, return null.
                return null;
            }

            if (task.has(taskReferenceName)) {
                break;
            }
        }
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public WorkflowTask getTaskByRefName(String taskReferenceName) {
        return collectTasks().stream()
                .filter(
                        workflowTask ->
                                workflowTask.getTaskReferenceName().equals(taskReferenceName))
                .findFirst()
                .orElse(null);
    }

    public List<WorkflowTask> collectTasks() {
        List<WorkflowTask> tasks = new LinkedList<>();
        for (WorkflowTask workflowTask : this.tasks) {
            tasks.addAll(workflowTask.collectTasks());
        }
        return tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WorkflowDef that = (WorkflowDef) o;
        return getVersion() == that.getVersion()
                && getSchemaVersion() == that.getSchemaVersion()
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getDescription(), that.getDescription())
                && Objects.equals(getTasks(), that.getTasks())
                && Objects.equals(getInputParameters(), that.getInputParameters())
                && Objects.equals(getOutputParameters(), that.getOutputParameters())
                && Objects.equals(getFailureWorkflow(), that.getFailureWorkflow())
                && Objects.equals(getOwnerEmail(), that.getOwnerEmail())
                && Objects.equals(getTimeoutSeconds(), that.getTimeoutSeconds());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getName(),
                getDescription(),
                getVersion(),
                getTasks(),
                getInputParameters(),
                getOutputParameters(),
                getFailureWorkflow(),
                getSchemaVersion(),
                getOwnerEmail(),
                getTimeoutSeconds());
    }

    @Override
    public String toString() {
        return "WorkflowDef{"
                + "name='"
                + name
                + '\''
                + ", description='"
                + description
                + '\''
                + ", version="
                + version
                + ", tasks="
                + tasks
                + ", inputParameters="
                + inputParameters
                + ", outputParameters="
                + outputParameters
                + ", failureWorkflow='"
                + failureWorkflow
                + '\''
                + ", schemaVersion="
                + schemaVersion
                + ", restartable="
                + restartable
                + ", workflowStatusListenerEnabled="
                + workflowStatusListenerEnabled
                + ", timeoutSeconds="
                + timeoutSeconds
                + '}';
    }
}
