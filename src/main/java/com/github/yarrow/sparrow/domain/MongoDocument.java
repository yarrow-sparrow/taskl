package com.github.yarrow.sparrow.domain;

/**
 * Interface for generalized entity processing, used for generating UUID v7 in GenerateEntityIdEventListener
 */
public interface MongoDocument {

    String getId();

    void setId(String id);
}
