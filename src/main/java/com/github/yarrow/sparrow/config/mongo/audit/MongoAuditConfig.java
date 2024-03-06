package com.github.yarrow.sparrow.config.mongo.audit;

import java.time.Clock;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
@EnableMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MongoAuditConfig {

    private final Clock clock;

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(
                List.of(
                    new InstantToEpochMilliConverter(),
                    new EpochMilliToInstantConverter()
                )
        );
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return new ClockDataTimeProvider(clock);
    }
}
