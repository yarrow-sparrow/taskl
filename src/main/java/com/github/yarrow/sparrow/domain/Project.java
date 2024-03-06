package com.github.yarrow.sparrow.domain;

import com.github.yarrow.sparrow.domain.annotation.CreatedTs;
import com.github.yarrow.sparrow.domain.annotation.UpdatedTs;
import java.time.Instant;
import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Key is used-defined alias for task
 */
@Data
@Builder
@Document
public class Project implements MongoDocument {

    @Id
    private String id;
    @Indexed(unique = true)
    private String key;
    @Version
    private Long version;
    @CreatedTs
    private Instant createdTs;
    @UpdatedTs
    private Instant updatedTs;
    private String name;
    private String description;
    @Indexed
    private Collection<String> memberUserIds;
}
