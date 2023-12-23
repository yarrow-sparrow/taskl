package com.github.straightth.domain;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Project {

    @Id
    private String id;
    private String name;
    private String description;
    @Indexed
    private Collection<String> memberUserIds;
}
