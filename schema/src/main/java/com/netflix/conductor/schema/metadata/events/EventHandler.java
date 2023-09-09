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
package com.netflix.conductor.schema.metadata.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** Defines an event handler */
@Data
@NoArgsConstructor
public class EventHandler {

    @NotEmpty(message = "Missing event handler name")
    private String name;

    @NotEmpty(message = "Missing event location")
    private String event;

    private String condition;

    @NotNull
    @NotEmpty(message = "No actions specified. Please specify at-least one action")
    private List<@Valid Action> actions = new LinkedList<>();

    private boolean active;

    private String evaluatorType;

    @Data
    public static class Action {

        public enum Type {
            start_workflow,
            complete_task,
            fail_task
        }

        private Type action;

        private StartWorkflow start_workflow;

        private TaskDetails complete_task;

        private TaskDetails fail_task;

        private boolean expandInlineJSON;

    }


    @Data
    public static class TaskDetails {

        private String workflowId;

        private String taskRefName;

        private Map<String, Object> output = new HashMap<>();

        private Object outputMessage;

        private String taskId;

    }


    @Data
    public static class StartWorkflow {

        private String name;

        private Integer version;

        private String correlationId;

        private Map<String, Object> input = new HashMap<>();

        private Object inputMessage;

        private Map<String, String> taskToDomain;

    }
}
