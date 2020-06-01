package com.example.ozymaxx.redisdemo.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.collection.Stream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

@SpringBootTest
public class DemoCircuitBreakerTest {
    private static final String FALLBACK_RESULT = "fallback result";
    private static final String SUCCESSFUL_RESULT = "successfully returned";

    @Autowired
    private DemoCircuitBreaker demoCircuitBreaker;

    @BeforeEach
    public void resetCircuitBreaker() {
        final CircuitBreaker circuitBreaker = demoCircuitBreaker.getCircuitBreaker();
        circuitBreaker.reset();
        Assert.assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    public void verifyCircuitBreakerLoadedProperly() {
        final CircuitBreaker circuitBreaker = demoCircuitBreaker.getCircuitBreaker();
        final CircuitBreakerConfig circuitBreakerConfig = circuitBreaker.getCircuitBreakerConfig();
        Assert.assertEquals(
                CircuitBreakerConfig.SlidingWindowType.COUNT_BASED, circuitBreakerConfig.getSlidingWindowType());
        Assert.assertEquals(500, circuitBreakerConfig.getSlidingWindowSize());
        Assert.assertEquals(50, circuitBreakerConfig.getMinimumNumberOfCalls());
        Assert.assertEquals(60, circuitBreakerConfig.getPermittedNumberOfCallsInHalfOpenState());
        Assert.assertTrue(circuitBreakerConfig.isAutomaticTransitionFromOpenToHalfOpenEnabled());
        Assert.assertEquals(Duration.ofSeconds(5), circuitBreakerConfig.getWaitDurationInOpenState());
        Assert.assertEquals(50, circuitBreakerConfig.getFailureRateThreshold(), 1e-7);
        Assert.assertEquals(90, circuitBreakerConfig.getSlowCallRateThreshold(), 1e-7);
        Assert.assertEquals(Duration.ofMillis(60000), circuitBreakerConfig.getSlowCallDurationThreshold());
    }

    @Test
    public void verifyAfterSomeCallsCircuitBreakerIsOpen() {
        final CircuitBreaker circuitBreaker = demoCircuitBreaker.getCircuitBreaker();
        Assert.assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
        final CircuitBreakerConfig circuitBreakerConfig = circuitBreaker.getCircuitBreakerConfig();
        final int minNumAttempts = circuitBreakerConfig.getMinimumNumberOfCalls();
        Stream.range(0, minNumAttempts).forEach(count -> {
            Assert.assertEquals(count.intValue(), circuitBreaker.getMetrics().getNumberOfFailedCalls());
            final String result = demoCircuitBreaker.executeWithCallback(this::failure, this::fallback);
            Assert.assertEquals(FALLBACK_RESULT, result);
            Assert.assertEquals(0, circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
        });
        Assert.assertEquals(minNumAttempts, circuitBreaker.getMetrics().getNumberOfFailedCalls());
        Assert.assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }

    @Test
    public void verifyCircuitBreakerGetsClosedAfterSomeSuccessfulCalls() {
        final CircuitBreaker circuitBreaker = demoCircuitBreaker.getCircuitBreaker();
        circuitBreaker.transitionToOpenState();
        circuitBreaker.transitionToHalfOpenState();
        final CircuitBreakerConfig circuitBreakerConfig = circuitBreaker.getCircuitBreakerConfig();
        final int minNumAttemptsInHalfOpenState = circuitBreakerConfig.getMinimumNumberOfCalls();
        Stream.range(0, minNumAttemptsInHalfOpenState).forEach(count -> {
            Assert.assertEquals(CircuitBreaker.State.HALF_OPEN, circuitBreaker.getState());
            Assert.assertEquals(count.intValue(), circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
            final String result = demoCircuitBreaker.executeWithCallback(this::success, this::fallback);
            Assert.assertEquals(SUCCESSFUL_RESULT, result);
            Assert.assertEquals(0, circuitBreaker.getMetrics().getNumberOfFailedCalls());
        });
        Assert.assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    private String fallback(final Throwable throwable) {
        return FALLBACK_RESULT;
    }

    private String failure() {
        throw new IllegalArgumentException("an argument is illegal");
    }

    private String success() {
        return SUCCESSFUL_RESULT;
    }
}
