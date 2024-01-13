package com.github.straightth.config;

import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Storage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

@Lazy
@Configuration
public class MongoReplicationConfig {

    @Profile({"development", "test"})
    @Bean
    public MongodArguments mongodArguments() {
        return MongodArguments.builder()
                .replication(Storage.of("taskl", 10))
                .build();
    }
}
