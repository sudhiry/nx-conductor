package com.netflix.conductor.db.repository.listeners;

import com.netflix.conductor.db.models.MetaEventHandler;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MetaEventHandlerListener implements BeforeConvertCallback<MetaEventHandler> {
    @Override
    public MetaEventHandler onBeforeConvert(MetaEventHandler entity, String collection) {
        if(entity.getCreatedOn() == null) {
            entity.setCreatedOn(new Date());
        }
        entity.setModifiedOn(new Date());
        return entity;
    }
}