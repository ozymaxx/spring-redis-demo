package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.configuration.RedisConfigurationTestUtilities;
import com.example.ozymaxx.redisdemo.service.BackendA;
import com.example.ozymaxx.redisdemo.service.dto.ResponseA;
import com.example.ozymaxx.redisdemo.service.dto.ResponseAUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private ObjectMapper objectMapper;

    private static final String LONG_TEST_VALUE = "this-input-is-longer-than-32-characters";
    private static final String TEST_VALUE = "test-value";
    private static final String NEW_TEST_VALUE = "new-test-value";
    private static final String REDIS_CACHE_NAME = "sample-redis-cache";

    @BeforeEach
    public void beforeEach() {
        controllerA.evictCache();
    }

    @Test
    public void verifyControllerAReturnsCorrectValue() throws Exception {
        mockMvc.perform(get(String.format("/backendA/unconditional/%s", TEST_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{'value': '%s'}", TEST_VALUE)));
    }

    @Test
    public void verifyControllerAConditionalReturnsCorrectValue() throws Exception {
        mockMvc.perform(get(String.format("/backendA/conditional/%s", TEST_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{'value': '%s'}", TEST_VALUE)));
    }

    @Test
    public void verifyControllerUpdateAReturnsCorrectValue() throws Exception {
        final String requestAsJsonString = objectMapper.writeValueAsString(new ResponseAUpdate(NEW_TEST_VALUE));
        mockMvc.perform(post(String.format("/backendA/unconditional/%s", TEST_VALUE))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestAsJsonString))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{'value': '%s'}", NEW_TEST_VALUE)));
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

    @Test
    public void verifyRequestsCachedProperlyInBackendAConditional() {
        ResponseA responseA = controllerA.backendAConditional(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, responseA.getValue());
        responseA = controllerA.backendAConditional(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, responseA.getValue());
        verify(backendA, times(1)).method(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, ((ResponseA) retrieveCachedResponse(TEST_VALUE)).getValue());
    }

    @Test
    public void verifyRequestsNotCachedWhenLongInputGivenToBackendAConditional() {
        ResponseA responseA = controllerA.backendAConditional(LONG_TEST_VALUE);
        Assert.assertEquals(LONG_TEST_VALUE, responseA.getValue());
        responseA = controllerA.backendAConditional(LONG_TEST_VALUE);
        Assert.assertEquals(LONG_TEST_VALUE, responseA.getValue());
        verify(backendA, times(2)).method(LONG_TEST_VALUE);
        Assert.assertNull(retrieveCachedResponse(LONG_TEST_VALUE));
    }

    @Test
    public void verifyUpdateEvictsCache() {
        ResponseA responseA = controllerA.backendA(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, responseA.getValue());
        ResponseA newResponseA = controllerA.backendAUpdate(TEST_VALUE, new ResponseAUpdate(NEW_TEST_VALUE));
        Assert.assertEquals(NEW_TEST_VALUE, newResponseA.getValue());
        Assert.assertNull(retrieveCachedResponse(TEST_VALUE));
        responseA = controllerA.backendA(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, responseA.getValue());
        Assert.assertEquals(TEST_VALUE, ((ResponseA) retrieveCachedResponse(TEST_VALUE)).getValue());
        verify(backendA, times(3)).method(TEST_VALUE);
    }

    private Object retrieveCachedResponse(final String key) {
        return redisTemplate.opsForValue().get(String.format("%s::%s", REDIS_CACHE_NAME, key));
    }
}
