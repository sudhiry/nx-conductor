package com.netflix.conductor.rest.models;

import com.netflix.conductor.common.metadata.tasks.Task;
import lombok.Data;

import java.util.List;

@Data
public class WorkflowStatusResponse {

    Boolean isTerminated;

    List<Task> tasksInProgress;

}
