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
package com.netflix.conductor.core.execution;

import com.netflix.conductor.common.metadata.workflow.StartWorkflowRequest;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
public class StartWorkflowInput {

    private String name;
    private Integer version;
    private WorkflowDef workflowDefinition;
    private Map<String, Object> workflowInput;
    private String externalInputPayloadStoragePath;
    private String correlationId;
    private Integer priority;
    private String parentWorkflowId;
    private String parentWorkflowTaskId;
    private String event;
    private Map<String, String> taskToDomain;
    private String workflowId;
    private String triggeringWorkflowId;

    public StartWorkflowInput(StartWorkflowRequest startWorkflowRequest) {
        this.name = startWorkflowRequest.getName();
        this.version = startWorkflowRequest.getVersion();
        this.workflowDefinition = startWorkflowRequest.getWorkflowDef();
        this.correlationId = startWorkflowRequest.getCorrelationId();
        this.priority = startWorkflowRequest.getPriority();
        this.workflowInput = startWorkflowRequest.getInput();
        this.externalInputPayloadStoragePath =
                startWorkflowRequest.getExternalInputPayloadStoragePath();
        this.taskToDomain = startWorkflowRequest.getTaskToDomain();
    }

}
