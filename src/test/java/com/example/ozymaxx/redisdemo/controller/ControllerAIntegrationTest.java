package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.configuration.TestRedisConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllerAIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String TEST_VALUE = "test-value";

    @Test
    public void verifyControllerAReturnsCorrectValue() throws Exception {
        mockMvc.perform(get(String.format("/backendA/%s", TEST_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{'value': '%s'}", TEST_VALUE)));
    }
}
