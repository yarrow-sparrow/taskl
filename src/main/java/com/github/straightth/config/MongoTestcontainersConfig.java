package com.github.straightth.config;

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet;
import com.github.straightth.config.property.MongoApplicationProperties;
import com.mongodb.ConnectionString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Using Testcontainers to run the application without starting database manually
 */
@Slf4j
@Configuration
@RequiredArgsConstructor(onConstructor_ = @ConstructorBinding)
public class MongoTestcontainersConfig {

    private final MongoApplicationProperties mongoApplicationProperties;

    @Bean(destroyMethod = "close")
    public MongoDbReplicaSet mongoDbReplicaSet() {
        log.info("Mongo image: {}", mongoApplicationProperties.getImage());

        var mongoDbReplicaSet = MongoDbReplicaSet.builder()
                .mongoDockerImageName(mongoApplicationProperties.getImage())
                .useHostDockerInternal(true)
                .replicaSetNumber(1)
                .build();
        mongoDbReplicaSet.start();
        return mongoDbReplicaSet;
    }

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoClientSettingsBuilderCustomizer(
            MongoDbReplicaSet mongoDbReplicaSet
    ) {
        return settings -> settings.applyConnectionString(new ConnectionString(mongoDbReplicaSet.getReplicaSetUrl()));
    }
}
