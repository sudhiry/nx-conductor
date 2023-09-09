/*
 * Copyright 2021 Netflix, Inc.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.netflix.conductor.schema.metadata.tasks.TaskDef;
import com.netflix.conductor.schema.metadata.tasks.TaskType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;


import java.util.*;

/**
 * This is the task definition defined as part of the {@link WorkflowDef}. The tasks defined in
 * the Workflow definition are saved as part of {@link WorkflowDef#getTasks}
 */

@Data
public class WorkflowTask {

    @NotEmpty(message = "WorkflowTask name cannot be empty or null")
    private String name;

    @NotEmpty(message = "WorkflowTask taskReferenceName name cannot be empty or null")
    private String taskReferenceName;

    private String description;

    private Map<String, Object> inputParameters = new HashMap<>();

    private String type = TaskType.SIMPLE.name();

    private String dynamicTaskNameParam;

    @Deprecated
    private String caseValueParam;

    @Deprecated
    private String caseExpression;

    private String scriptExpression;

    @Data
    public static class WorkflowTaskList {

        private List<WorkflowTask> tasks;
    }

    // Populates for the tasks of the decision type
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, @Valid List<@Valid WorkflowTask>> decisionCases = new LinkedHashMap<>();

    @Deprecated private String dynamicForkJoinTasksParam;

    private String dynamicForkTasksParam;

    private String dynamicForkTasksInputParamName;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@Valid WorkflowTask> defaultCase = new LinkedList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@Valid List<@Valid WorkflowTask>> forkTasks = new LinkedList<>();

    @PositiveOrZero
    private int startDelay; // No. of seconds (at-least) to wait before starting a task.

    @Valid
    private SubWorkflowParams subWorkflowParam;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> joinOn = new LinkedList<>();

    private String sink;

    private boolean optional = false;

    private TaskDef taskDefinition;

    private Boolean rateLimited;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> defaultExclusiveJoinTask = new LinkedList<>();

    private Boolean asyncComplete = false;

    private String loopCondition;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<WorkflowTask> loopOver = new LinkedList<>();

    private Integer retryCount;

    private String evaluatorType;

    private String expression;


    private Collection<List<WorkflowTask>> children() {
        Collection<List<WorkflowTask>> workflowTaskLists = new LinkedList<>();

        switch (TaskType.of(type)) {
            case DECISION, SWITCH -> {
                workflowTaskLists.addAll(decisionCases.values());
                workflowTaskLists.add(defaultCase);
            }
            case FORK_JOIN -> workflowTaskLists.addAll(forkTasks);
            case DO_WHILE -> workflowTaskLists.add(loopOver);
            default -> {
            }
        }
        return workflowTaskLists;
    }

    public List<WorkflowTask> collectTasks() {
        List<WorkflowTask> tasks = new LinkedList<>();
        tasks.add(this);
        for (List<WorkflowTask> workflowTaskList : children()) {
            for (WorkflowTask workflowTask : workflowTaskList) {
                tasks.addAll(workflowTask.collectTasks());
            }
        }
        return tasks;
    }

    public WorkflowTask next(String taskReferenceName, WorkflowTask parent) {
        TaskType taskType = TaskType.of(type);

        switch (taskType) {
            case DO_WHILE, DECISION, SWITCH -> {
                for (List<WorkflowTask> workflowTasks : children()) {
                    Iterator<WorkflowTask> iterator = workflowTasks.iterator();
                    while (iterator.hasNext()) {
                        WorkflowTask task = iterator.next();
                        if (task.getTaskReferenceName().equals(taskReferenceName)) {
                            break;
                        }
                        WorkflowTask nextTask = task.next(taskReferenceName, this);
                        if (nextTask != null) {
                            return nextTask;
                        }
                        if (task.has(taskReferenceName)) {
                            break;
                        }
                    }
                    if (iterator.hasNext()) {
                        return iterator.next();
                    }
                }
                if (taskType == TaskType.DO_WHILE && this.has(taskReferenceName)) {
                    // come here means this is DO_WHILE task and `taskReferenceName` is the last
                    // task in
                    // this DO_WHILE task, because DO_WHILE task need to be executed to decide
                    // whether to
                    // schedule next iteration, so we just return the DO_WHILE task, and then ignore
                    // generating this task again in deciderService.getNextTask()
                    return this;
                }
            }
            case FORK_JOIN -> {
                boolean found = false;
                for (List<WorkflowTask> workflowTasks : children()) {
                    Iterator<WorkflowTask> iterator = workflowTasks.iterator();
                    while (iterator.hasNext()) {
                        WorkflowTask task = iterator.next();
                        if (task.getTaskReferenceName().equals(taskReferenceName)) {
                            found = true;
                            break;
                        }
                        WorkflowTask nextTask = task.next(taskReferenceName, this);
                        if (nextTask != null) {
                            return nextTask;
                        }
                        if (task.has(taskReferenceName)) {
                            break;
                        }
                    }
                    if (iterator.hasNext()) {
                        return iterator.next();
                    }
                    if (found && parent != null) {
                        return parent.next(
                                this.taskReferenceName,
                                parent); // we need to return join task... -- get my sibling from my
                        // parent..
                    }
                }
            }
            case DYNAMIC, TERMINATE, SIMPLE -> {
                return null;
            }
            default -> {
            }
        }
        return null;
    }

    public boolean has(String taskReferenceName) {
        if (this.getTaskReferenceName().equals(taskReferenceName)) {
            return true;
        }

        switch (TaskType.of(type)) {
            case DECISION, SWITCH, DO_WHILE, FORK_JOIN -> {
                for (List<WorkflowTask> childList : children()) {
                    for (WorkflowTask child : childList) {
                        if (child.has(taskReferenceName)) {
                            return true;
                        }
                    }
                }
            }
            default -> {
            }
        }
        return false;
    }

    public WorkflowTask get(String taskReferenceName) {

        if (this.getTaskReferenceName().equals(taskReferenceName)) {
            return this;
        }
        for (List<WorkflowTask> childList : children()) {
            for (WorkflowTask child : childList) {
                WorkflowTask found = child.get(taskReferenceName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name + "/" + taskReferenceName;
    }
}
