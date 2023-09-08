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
package com.netflix.conductor.common.metadata.tasks;

import com.netflix.conductor.common.constraints.OwnerEmailMandatoryConstraint;
import com.netflix.conductor.common.constraints.TaskTimeoutConstraint;
import com.netflix.conductor.common.metadata.BaseDef;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.util.*;

@EqualsAndHashCode(callSuper = true)
@TaskTimeoutConstraint
@Valid
@Data
public class TaskDef extends BaseDef {

    public enum TimeoutPolicy {
        RETRY,
        TIME_OUT_WF,
        ALERT_ONLY
    }

    public enum RetryLogic {
        FIXED,
        EXPONENTIAL_BACKOFF,
        LINEAR_BACKOFF
    }

    public static final int ONE_HOUR = 60 * 60;

    /** Unique name identifying the task. The name is unique across */
    @NotEmpty(message = "TaskDef name cannot be null or empty")
    private String name;

    private String description;

    @Min(value = 0, message = "TaskDef retryCount: {value} must be >= 0")
    @Max(value = 10, message = "TaskDef retryCount: ${validatedValue} must be <= {value}")
    private int retryCount = 3; // Default

    @NotNull
    private long timeoutSeconds;

    private List<String> inputKeys = new ArrayList<>();

    private List<String> outputKeys = new ArrayList<>();

    private TimeoutPolicy timeoutPolicy = TimeoutPolicy.TIME_OUT_WF;

    private RetryLogic retryLogic = RetryLogic.FIXED;

    private int retryDelaySeconds = 60;

    @Min(
            value = 1,
            message =
                    "TaskDef responseTimeoutSeconds: ${validatedValue} should be minimum {value} second")
    private long responseTimeoutSeconds = ONE_HOUR;

    private Integer concurrentExecLimit;

    private Map<String, Object> inputTemplate = new HashMap<>();

    // This field is deprecated, do not use id 13.
    //	@ProtoField(id = 13)
    //	private Integer rateLimitPerSecond;

    private Integer rateLimitPerFrequency;

    private Integer rateLimitFrequencyInSeconds;

    private String isolationGroupId;

    private String executionNameSpace;

    @OwnerEmailMandatoryConstraint
    @Email(message = "ownerEmail should be valid email address")
    private String ownerEmail;

    @Min(value = 0, message = "TaskDef pollTimeoutSeconds: {value} must be >= 0")
    private Integer pollTimeoutSeconds;

    @Min(value = 1, message = "Backoff scale factor. Applicable for LINEAR_BACKOFF")
    private Integer backoffScaleFactor = 1;

    public TaskDef() {}

    public TaskDef(String name) {
        this.name = name;
    }

    public TaskDef(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public TaskDef(String name, String description, int retryCount, long timeoutSeconds) {
        this.name = name;
        this.description = description;
        this.retryCount = retryCount;
        this.timeoutSeconds = timeoutSeconds;
    }

    public TaskDef(
            String name,
            String description,
            String ownerEmail,
            int retryCount,
            long timeoutSeconds,
            long responseTimeoutSeconds) {
        this.name = name;
        this.description = description;
        this.ownerEmail = ownerEmail;
        this.retryCount = retryCount;
        this.timeoutSeconds = timeoutSeconds;
        this.responseTimeoutSeconds = responseTimeoutSeconds;
    }

    public int concurrencyLimit() {
        return concurrentExecLimit == null ? 0 : concurrentExecLimit;
    }
}
