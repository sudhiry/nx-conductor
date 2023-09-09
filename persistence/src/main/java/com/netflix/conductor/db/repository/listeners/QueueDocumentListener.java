package com.netflix.conductor.db.repository.listeners;

import com.netflix.conductor.db.models.QueueDocument;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class QueueDocumentListener implements BeforeConvertCallback<QueueDocument> {

    @Override
    public QueueDocument onBeforeConvert(QueueDocument entity, String collection) {
        if(entity.getCreatedOn() == null) {
            entity.setCreatedOn(new Date());
        }
        entity.setModifiedOn(new Date());
        return entity;
    }
}
