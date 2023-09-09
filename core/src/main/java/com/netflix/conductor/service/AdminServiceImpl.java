package com.netflix.conductor.service;

import com.netflix.conductor.annotations.Audit;
import com.netflix.conductor.annotations.Trace;
import com.netflix.conductor.schema.metadata.tasks.Task;
import com.netflix.conductor.core.config.ConductorProperties;
import com.netflix.conductor.core.reconciliation.WorkflowRepairService;
import com.netflix.conductor.core.utils.Utils;
import com.netflix.conductor.dao.QueueDAO;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

import java.util.*;

@Audit
@Trace
@Service
public class AdminServiceImpl implements AdminService {

    private final ConductorProperties properties;
    private final ExecutionService executionService;
    private final QueueDAO queueDAO;
    private final WorkflowRepairService workflowRepairService;
    private final BuildProperties buildProperties;

    public AdminServiceImpl(
            ConductorProperties properties,
            ExecutionService executionService,
            QueueDAO queueDAO,
            Optional<WorkflowRepairService> workflowRepairService,
            Optional<BuildProperties> buildProperties) {
        this.properties = properties;
        this.executionService = executionService;
        this.queueDAO = queueDAO;
        this.workflowRepairService = workflowRepairService.orElse(null);
        this.buildProperties = buildProperties.orElse(null);
    }

    /**
     * Get all the configuration parameters.
     *
     * @return all the configuration parameters.
     */
    public Map<String, Object> getAllConfig() {
        Map<String, Object> configs = properties.getAll();
        configs.putAll(getBuildProperties());
        return configs;
    }

    /**
     * Get all build properties
     *
     * @return all the build properties.
     */
    private Map<String, Object> getBuildProperties() {
        if (buildProperties == null) return Collections.emptyMap();
        Map<String, Object> buildProps = new HashMap<>();
        buildProps.put("version", buildProperties.getVersion());
        buildProps.put("buildDate", buildProperties.getTime());
        return buildProps;
    }

    /**
     * Get the list of pending tasks for a given task type.
     *
     * @param taskType Name of the task
     * @param start Start index of pagination
     * @param count Number of entries
     * @return list of pending {@link Task}
     */
    public List<Task> getListOfPendingTask(String taskType, Integer start, Integer count) {
        List<Task> tasks = executionService.getPendingTasksForTaskType(taskType);
        int total = start + count;
        total = Math.min(tasks.size(), total);
        if (start > tasks.size()) {
            start = tasks.size();
        }
        return tasks.subList(start, total);
    }

    @Override
    public boolean verifyAndRepairWorkflowConsistency(String workflowId) {
        if (workflowRepairService == null) {
            throw new IllegalStateException(
                    WorkflowRepairService.class.getSimpleName() + " is disabled.");
        }
        return workflowRepairService.verifyAndRepairWorkflow(workflowId, true);
    }

    /**
     * Queue up the workflow for sweep.
     *
     * @param workflowId Id of the workflow
     * @return the id of the workflow instance that can be use for tracking.
     */
    public String requeueSweep(String workflowId) {
        boolean pushed =
                queueDAO.pushIfNotExists(
                        Utils.DECIDER_QUEUE,
                        workflowId,
                        properties.getWorkflowOffsetTimeout().getSeconds());
        return pushed + "." + workflowId;
    }

    /**
     * Get registered queues.
     *
     * @param verbose `true|false` for verbose logs
     * @return map of event queues
     */
//    public Map<String, ?> getEventQueues(boolean verbose) {
//        if (eventQueueManager == null) {
//            throw new IllegalStateException("Event processing is DISABLED");
//        }
//        return (verbose ? eventQueueManager.getQueueSizes() : eventQueueManager.getQueues());
//    }
}
