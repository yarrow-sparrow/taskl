package com.github.yarrow.sparrow.domain.listener;

import com.github.f4b6a3.uuid.alt.GUID;
import com.github.yarrow.sparrow.domain.MongoDocument;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

/**
 * Generates UUID v7 entity ID
 * ClassHealthTest ensures that every Mongo entity implements MongoDocument interface
 */
public class GenerateEntityIdEventListener extends AbstractMongoEventListener<MongoDocument> {

    @Override
    public void onBeforeConvert(@NotNull BeforeConvertEvent<MongoDocument> event) {
        super.onBeforeConvert(event);
        var entity = event.getSource();
        if (event.getSource().getId() == null) {
            entity.setId(GUID.v7().toString());
        }
    }
}
