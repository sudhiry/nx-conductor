package com.netflix.conductor.db.repository.listeners;

import com.netflix.conductor.db.models.WorkflowToTask;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class WorkflowToTaskListener implements BeforeConvertCallback<WorkflowToTask> {
    @Override
    public WorkflowToTask onBeforeConvert(WorkflowToTask entity, String collection) {
        if(entity.getCreatedOn() == null) {
            entity.setCreatedOn(new Date());
        }
        entity.setModifiedOn(new Date());
        return entity;
    }
}
