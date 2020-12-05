package com.example.ozymaxx.redisdemo.configuration;

import com.example.ozymaxx.api.SampleApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleServiceConfig {
    @Bean
    public SampleApi sampleApi() {
        return new SampleApi();
    }
}
