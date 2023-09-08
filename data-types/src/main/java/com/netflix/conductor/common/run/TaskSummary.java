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
package com.netflix.conductor.common.run;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.Task.Status;
import com.netflix.conductor.common.utils.SummaryUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@Data
@NoArgsConstructor
public class TaskSummary {

    /** The time should be stored as GMT */
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private String workflowId;

    private String workflowType;

    private String correlationId;

    private String scheduledTime;

    private String startTime;

    private String updateTime;

    private String endTime;

    private Task.Status status;

    private String reasonForIncompletion;

    private long executionTime;

    private long queueWaitTime;

    private String taskDefName;

    private String taskType;

    private String input;

    private String output;

    private String taskId;

    private String externalInputPayloadStoragePath;

    private String externalOutputPayloadStoragePath;

    private int workflowPriority;

    private String domain;

    public TaskSummary(Task task) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(GMT);

        this.taskId = task.getTaskId();
        this.taskDefName = task.getTaskDefName();
        this.taskType = task.getTaskType();
        this.workflowId = task.getWorkflowInstanceId();
        this.workflowType = task.getWorkflowType();
        this.workflowPriority = task.getWorkflowPriority();
        this.correlationId = task.getCorrelationId();
        this.scheduledTime = sdf.format(new Date(task.getScheduledTime()));
        this.startTime = sdf.format(new Date(task.getStartTime()));
        this.updateTime = sdf.format(new Date(task.getUpdateTime()));
        this.endTime = sdf.format(new Date(task.getEndTime()));
        this.status = task.getStatus();
        this.reasonForIncompletion = task.getReasonForIncompletion();
        this.queueWaitTime = task.getQueueWaitTime();
        this.domain = task.getDomain();
        if (task.getInputData() != null) {
            this.input = SummaryUtil.serializeInputOutput(task.getInputData());
        }

        if (task.getOutputData() != null) {
            this.output = SummaryUtil.serializeInputOutput(task.getOutputData());
        }

        if (task.getEndTime() > 0) {
            this.executionTime = task.getEndTime() - task.getStartTime();
        }

        if (StringUtils.isNotBlank(task.getExternalInputPayloadStoragePath())) {
            this.externalInputPayloadStoragePath = task.getExternalInputPayloadStoragePath();
        }
        if (StringUtils.isNotBlank(task.getExternalOutputPayloadStoragePath())) {
            this.externalOutputPayloadStoragePath = task.getExternalOutputPayloadStoragePath();
        }
    }

}
