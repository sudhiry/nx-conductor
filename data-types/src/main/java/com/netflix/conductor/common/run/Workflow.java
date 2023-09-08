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
package com.netflix.conductor.common.run;

import com.netflix.conductor.common.metadata.Auditable;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Workflow extends Auditable {

    public enum WorkflowStatus {
        RUNNING(false, false),
        COMPLETED(true, true),
        FAILED(true, false),
        TIMED_OUT(true, false),
        TERMINATED(true, false),
        PAUSED(false, true);

        private final boolean terminal;

        private final boolean successful;

        WorkflowStatus(boolean terminal, boolean successful) {
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

    private WorkflowStatus status = WorkflowStatus.RUNNING;

    private long endTime;

    private String workflowId;

    private String parentWorkflowId;

    private String parentWorkflowTaskId;

    private List<Task> tasks = new LinkedList<>();

    private Map<String, Object> input = new HashMap<>();

    private Map<String, Object> output = new HashMap<>();

    private String correlationId;

    private String reRunFromWorkflowId;

    private String reasonForIncompletion;

    private String event;

    private Map<String, String> taskToDomain = new HashMap<>();

    private Set<String> failedReferenceTaskNames = new HashSet<>();

    private WorkflowDef workflowDefinition;

    private String externalInputPayloadStoragePath;

    private String externalOutputPayloadStoragePath;

    @Min(value = 0, message = "workflow priority: ${validatedValue} should be minimum {value}")
    @Max(value = 99, message = "workflow priority: ${validatedValue} should be maximum {value}")
    private int priority;

    private Map<String, Object> variables = new HashMap<>();

    private long lastRetriedTime;

    private Set<String> failedTaskNames = new HashSet<>();

    public long getStartTime() {
        return getCreateTime();
    }

    public void setInput(Map<String, Object> input) {
        if (input == null) {
            input = new HashMap<>();
        }
        this.input = input;
    }

    public void setPriority(int priority) {
        if (priority < 0 || priority > 99) {
            throw new IllegalArgumentException("priority MUST be between 0 and 99 (inclusive)");
        }
        this.priority = priority;
    }

    public String getWorkflowName() {
        if (workflowDefinition == null) {
            throw new NullPointerException("Workflow definition is null");
        }
        return workflowDefinition.getName();
    }

    public int getWorkflowVersion() {
        if (workflowDefinition == null) {
            throw new NullPointerException("Workflow definition is null");
        }
        return workflowDefinition.getVersion();
    }

    public boolean hasParent() {
        return StringUtils.isNotEmpty(parentWorkflowId);
    }

    public Task getTaskByRefName(String refName) {
        if (refName == null) {
            throw new RuntimeException(
                    "refName passed is null.  Check the workflow execution.  For dynamic tasks, make sure referenceTaskName is set to a not null value");
        }
        LinkedList<Task> found = new LinkedList<>();
        for (Task t : tasks) {
            if (t.getReferenceTaskName() == null) {
                throw new RuntimeException(
                        "Task "
                                + t.getTaskDefName()
                                + ", seq="
                                + t.getSeq()
                                + " does not have reference name specified.");
            }
            if (t.getReferenceTaskName().equals(refName)) {
                found.add(t);
            }
        }
        if (found.isEmpty()) {
            return null;
        }
        return found.getLast();
    }

    /**
     * @return a deep copy of the workflow instance
     */
    public Workflow copy() {
        Workflow copy = new Workflow();
        copy.setInput(input);
        copy.setOutput(output);
        copy.setStatus(status);
        copy.setWorkflowId(workflowId);
        copy.setParentWorkflowId(parentWorkflowId);
        copy.setParentWorkflowTaskId(parentWorkflowTaskId);
        copy.setReRunFromWorkflowId(reRunFromWorkflowId);
        copy.setCorrelationId(correlationId);
        copy.setEvent(event);
        copy.setReasonForIncompletion(reasonForIncompletion);
        copy.setWorkflowDefinition(workflowDefinition);
        copy.setPriority(priority);
        copy.setTasks(tasks.stream().map(Task::deepCopy).collect(Collectors.toList()));
        copy.setVariables(variables);
        copy.setEndTime(endTime);
        copy.setLastRetriedTime(lastRetriedTime);
        copy.setTaskToDomain(taskToDomain);
        copy.setFailedReferenceTaskNames(failedReferenceTaskNames);
        copy.setFailedTaskNames(failedTaskNames);
        copy.setExternalInputPayloadStoragePath(externalInputPayloadStoragePath);
        copy.setExternalOutputPayloadStoragePath(externalOutputPayloadStoragePath);
        return copy;
    }

    @Override
    public String toString() {
        String name = workflowDefinition != null ? workflowDefinition.getName() : null;
        Integer version = workflowDefinition != null ? workflowDefinition.getVersion() : null;
        return String.format("%s.%s/%s.%s", name, version, workflowId, status);
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
}
