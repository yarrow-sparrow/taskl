package com.github.straightth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.straightth.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class MockMvcAbstractTest {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;

    protected String getMockedUserId() {
        return SecurityUtil.getCurrentUserId();
    }
}
