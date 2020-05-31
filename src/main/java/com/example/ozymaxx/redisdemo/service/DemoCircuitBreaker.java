package com.example.ozymaxx.redisdemo.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class DemoCircuitBreaker {
    private static final String BACKEND_B_NAME = "backendB";

    private CircuitBreaker circuitBreaker;

    public DemoCircuitBreaker(final CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(BACKEND_B_NAME);
    }

    public CircuitBreaker getCircuitBreaker() {
        return this.circuitBreaker;
    }

    public <T> T executeWithCallback(final Supplier<T> actualMethod, final Function<Throwable, T> fallbackMethod) {
        final Supplier<T> decoratedCall = this.circuitBreaker.decorateSupplier(actualMethod);
        return Try.of(decoratedCall::get).recover(fallbackMethod).get();
    }
}
