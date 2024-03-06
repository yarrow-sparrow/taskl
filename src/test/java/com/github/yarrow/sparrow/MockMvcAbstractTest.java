package com.github.yarrow.sparrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yarrow.sparrow.config.FixedClockConfig;
import com.github.yarrow.sparrow.util.SecurityUtil;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest(classes = FixedClockConfig.class)
@AutoConfigureMockMvc
@Transactional
public abstract class MockMvcAbstractTest {

    protected static final String RANDOM_UUID = UUID.randomUUID().toString();

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected Clock clock;

    protected String getMockedUserId() {
        return SecurityUtil.getCurrentUserId();
    }

    protected Instant getClockTsMillis() {
        return clock.instant().truncatedTo(ChronoUnit.MILLIS);
    }
}
