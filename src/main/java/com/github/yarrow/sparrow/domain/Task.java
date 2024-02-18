package com.github.yarrow.sparrow.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Task implements MongoDocument {

    @Id
    private String id;
    @Indexed
    private String projectId;
    private String name;
    private String description;
    private String assigneeUserId;
    private TaskStatus status;
    private Double storyPoints;
}
