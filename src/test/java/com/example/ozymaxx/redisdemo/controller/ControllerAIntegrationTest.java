package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.configuration.RedisConfigurationTestUtilities;
import com.example.ozymaxx.redisdemo.service.BackendA;
import com.example.ozymaxx.redisdemo.service.dto.ResponseA;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RedisConfigurationTestUtilities.class)
@AutoConfigureMockMvc
public class ControllerAIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ControllerA controllerA;

    @SpyBean
    private BackendA backendA;

    @Autowired
    private RedisTemplate redisTemplate;

    private static final String TEST_VALUE = "test-value";
    private static final String REDIS_CACHE_NAME = "sample-redis-cache";

    @BeforeEach
    public void beforeEach() {
        controllerA.evictCache();
    }

    @Test
    public void verifyControllerAReturnsCorrectValue() throws Exception {
        mockMvc.perform(get(String.format("/backendA/%s", TEST_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{'value': '%s'}", TEST_VALUE)));
    }

    @Test
    public void verifyRequestsCachedProperly() {
        ResponseA responseA = controllerA.backendA(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, responseA.getValue());
        responseA = controllerA.backendA(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, responseA.getValue());
        verify(backendA, times(1)).method(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, ((ResponseA) retrieveCachedResponse(TEST_VALUE)).getValue());
    }

    private Object retrieveCachedResponse(final String key) {
        return redisTemplate.opsForValue().get(String.format("%s::%s", REDIS_CACHE_NAME, key));
    }
}
