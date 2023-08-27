package com.netflix.conductor.rest.services;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskType;
import com.netflix.conductor.model.TaskModel;
import com.netflix.conductor.rest.dao.CustomExecutionDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomExecutionServices {

    private final CustomExecutionDAO customExecutionDAO;

    public CustomExecutionServices(CustomExecutionDAO customExecutionDAO) {
        super();
        this.customExecutionDAO = customExecutionDAO;
    }

    public List<Task> getInProgressTasksForWorkflow(String workflowId, TaskType taskType) {
        List<TaskModel> taskModels = customExecutionDAO.getPendingTasksByTaskType(taskType, workflowId);
        return taskModels.stream().map(TaskModel::toTask).toList();
    }

}
