package com.github.yarrow.sparrow.health;

import com.github.yarrow.sparrow.MockMvcAbstractTest;
import com.github.yarrow.sparrow.domain.MongoDocument;
import io.github.classgraph.ClassGraph;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;

public class ClassHealthTest extends MockMvcAbstractTest {

    @Test
    public void everyEntityImplementsMongoDocumentInterface() {
        try (var scanResult = new ClassGraph()
                .enableAllInfo()
                .acceptPackages("com.github.yarrow.sparrow")
                .scan()) {
            for (var classInfo : scanResult.getClassesWithAnnotation(Document.class)) {
                if (!classInfo.implementsInterface(MongoDocument.class)) {
                    throw new IllegalStateException("MongoDocument interface on class " + classInfo.getName()
                            + " is not found, every Mongo Document must implement it"
                    );
                }
            }
        }
    }
}
