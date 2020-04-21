package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.configuration.TestRedisConfiguration;
import com.example.ozymaxx.redisdemo.service.BackendA;
import com.example.ozymaxx.redisdemo.service.dto.ResponseA;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestRedisConfiguration.class)
public class ControllerACacheTest {

    private static final String TEST_VALUE = "test-value";
    private static final ResponseA EXPECTED_RESPONSE_A = new ResponseA(TEST_VALUE);

    @MockBean
    private BackendA backendA;

    @Autowired
    private ControllerA controllerA;

    @BeforeEach
    public void setUp() {
        controllerA.evictCache();
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
