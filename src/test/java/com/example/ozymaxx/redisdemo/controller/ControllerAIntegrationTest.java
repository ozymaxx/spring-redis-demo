package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.configuration.RedisConfigurationTestUtilities;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.cache.redis.port: 6370")
@AutoConfigureMockMvc
public class ControllerAIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_VALUE = "test-value";
    private static final int REDIS_SERVER_PORT = 6370;

    private static RedisConfigurationTestUtilities redisConfigurationTestUtilities;

    @BeforeAll
    public static void setUp() {
        redisConfigurationTestUtilities = new RedisConfigurationTestUtilities(REDIS_SERVER_PORT);
        redisConfigurationTestUtilities.launchRedisServer();
    }

    @AfterAll
    public static void tearDown() {
        redisConfigurationTestUtilities.stopRedisServer();
    }

    @Test
    public void verifyControllerAReturnsCorrectValue() throws Exception {
        mockMvc.perform(get(String.format("/backendA/%s", TEST_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{'value': '%s'}", TEST_VALUE)));
    }
}
