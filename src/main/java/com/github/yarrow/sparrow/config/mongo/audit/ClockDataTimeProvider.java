package com.github.yarrow.sparrow.config.mongo.audit;

import java.time.Clock;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.auditing.DateTimeProvider;

@AllArgsConstructor
public class ClockDataTimeProvider implements DateTimeProvider {

    private final Clock clock;

    @Override
    @NotNull
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(clock.instant());
    }
}
