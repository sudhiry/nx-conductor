package com.netflix.conductor.core.execution.tasks;

import org.springframework.stereotype.Component;

import static com.netflix.conductor.schema.metadata.tasks.TaskType.TASK_TYPE_HTTP;

@Component(TASK_TYPE_HTTP)
public class HttpTask extends WorkflowSystemTask {

    public HttpTask() {
        super(TASK_TYPE_HTTP);
    }

    // FIXME Implement HTTP TASK
}
