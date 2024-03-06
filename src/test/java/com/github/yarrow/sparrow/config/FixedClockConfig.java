package com.github.yarrow.sparrow.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FixedClockConfig {

    @Bean
    public Clock fixedClock() {
        return Mockito.spy(Clock.fixed(Instant.now(), ZoneId.systemDefault()));
    }
}
