package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.utilities.RedisConfigurationTestUtilities;
import com.example.ozymaxx.redisdemo.service.BackendA;
import com.example.ozymaxx.redisdemo.service.dto.ResponseA;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.cache.redis.port: 6370")
public class ControllerACacheTest {

    private static final String TEST_VALUE = "test-value";
    private static final ResponseA EXPECTED_RESPONSE_A = new ResponseA(TEST_VALUE);
    private static final int REDIS_SERVER_PORT = 6370;

    @MockBean
    private BackendA backendA;

    @Autowired
    private ControllerA controllerA;

    private static RedisConfigurationTestUtilities redisConfigurationTestUtilities;

    @BeforeAll
    public static void setUp() {
        redisConfigurationTestUtilities = new RedisConfigurationTestUtilities(REDIS_SERVER_PORT);
        redisConfigurationTestUtilities.launchRedisServer();
    }

    @BeforeEach
    public void setUpTestCase() {
        controllerA.evictCache();
    }

    @AfterAll
    public static void tearDown() {
        redisConfigurationTestUtilities.stopRedisServer();
    }

    @Test
    public void verifyRequestsCachedProperly() {
        when(backendA.method(eq(TEST_VALUE))).thenReturn(EXPECTED_RESPONSE_A);
        ResponseA responseA = controllerA.backendA(TEST_VALUE);
        Assert.assertEquals(EXPECTED_RESPONSE_A.getValue(), responseA.getValue());
        responseA = controllerA.backendA(TEST_VALUE);
        Assert.assertEquals(EXPECTED_RESPONSE_A.getValue(), responseA.getValue());
        verify(backendA, times(1)).method(TEST_VALUE);
    }
}
