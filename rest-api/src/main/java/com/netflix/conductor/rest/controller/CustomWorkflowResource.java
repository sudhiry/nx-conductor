package com.netflix.conductor.rest.controller;

import com.netflix.conductor.common.metadata.tasks.TaskType;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.rest.models.WorkflowStatusResponse;
import com.netflix.conductor.rest.services.CustomExecutionServices;
import com.netflix.conductor.service.WorkflowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/custom/workflow")
public class CustomWorkflowResource {

    private final CustomExecutionServices customExecutionServices;

    private final WorkflowService workflowService;

    public CustomWorkflowResource(
            WorkflowService workflowService,
            CustomExecutionServices customExecutionServices) {
        super();
        this.workflowService = workflowService;
        this.customExecutionServices = customExecutionServices;
    }

    @GetMapping("/{workflowId}/tasks-in-progress/{taskType}")
    public WorkflowStatusResponse getPendingHumanTasksForWorkflow(
            @PathVariable("workflowId") @NotNull String workflowId,
            @PathVariable("taskType") @NotNull TaskType taskType) {
        WorkflowStatusResponse workflowStatusResponse = new WorkflowStatusResponse();
        workflowStatusResponse.setIsTerminated(getWorkflowStatus(workflowId));
        if(!workflowStatusResponse.getIsTerminated()) {
            workflowStatusResponse.setTasksInProgress(customExecutionServices.getInProgressTasksForWorkflow(workflowId, taskType));
        }
        return workflowStatusResponse;
    }

    private boolean getWorkflowStatus(String workflowId) {
        Workflow workflow = workflowService.getExecutionStatus(workflowId, false);
        return workflow.getStatus().isTerminal();
    }

}
