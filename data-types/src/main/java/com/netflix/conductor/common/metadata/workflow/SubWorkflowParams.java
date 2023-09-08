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
package com.netflix.conductor.common.metadata.workflow;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class SubWorkflowParams {

    @NotNull(message = "SubWorkflowParams name cannot be null")
    @NotEmpty(message = "SubWorkflowParams name cannot be empty")
    private String name;

    private Integer version;

    private Map<String, String> taskToDomain;

    // workaround as WorkflowDef cannot directly be used due to cyclic dependency issue in protobuf
    // imports
    private Object workflowDefinition;

}
