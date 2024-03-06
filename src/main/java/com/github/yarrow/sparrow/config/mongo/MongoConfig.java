package com.github.yarrow.sparrow.config.mongo;

import com.github.yarrow.sparrow.domain.listener.GenerateEntityIdEventListener;
import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MongoConfig {

    @Profile({"development", "test"})
    @Bean
    public MongodArguments mongodArguments() {
        return MongodArguments.builder()
                .replication(Storage.of("taskl", 10))
                .build();
    }

    @Bean
    public GenerateEntityIdEventListener generateEntityIdEventListener() {
        return new GenerateEntityIdEventListener();
    }
}
