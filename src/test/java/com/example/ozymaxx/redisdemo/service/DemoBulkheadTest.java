package com.example.ozymaxx.redisdemo.service;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.vavr.collection.Stream;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class DemoBulkheadTest {
    private static final long LONGER_THAN_WAIT_DURATION = 2000;
    private static final long SHORTER_THAN_WAIT_DURATION = 250;
    private static final String FALLBACK_RESULT = "fallback result";
    private static final String SUCCESSFUL_RESULT = "successful result";

    @Autowired
    private DemoBulkhead demoBulkhead;

    @Test
    public void verifyBulkheadLoadedProperly() {
        final Bulkhead bulkhead = demoBulkhead.getBulkhead();
        final BulkheadConfig bulkheadConfig = bulkhead.getBulkheadConfig();
        Assert.assertEquals(7, bulkheadConfig.getMaxConcurrentCalls());
        Assert.assertEquals(Duration.ofMillis(500), bulkheadConfig.getMaxWaitDuration());
    }

    @Test
    public void verifyWhenBulkheadIsFullButCallsTerminateLateThenFallbackGetsExecuted() throws InterruptedException {
        verifyWhenBulkheadIsFullFallbackGetsExecuted(1, LONGER_THAN_WAIT_DURATION);
    }

    @Test
    public void verifyWhenBulkheadIsFullButCallsTerminateShortlyThenFallbackNeverGetsExecuted()
            throws InterruptedException {
        verifyWhenBulkheadIsFullFallbackGetsExecuted(0, SHORTER_THAN_WAIT_DURATION);
    }

    private void verifyWhenBulkheadIsFullFallbackGetsExecuted(
            final int numExpectedFailures, final long sleepDurationForTheFirstCall) throws InterruptedException {
        final Bulkhead bulkhead = demoBulkhead.getBulkhead();
        final BulkheadConfig bulkheadConfig = bulkhead.getBulkheadConfig();
        final int numAttempts = bulkheadConfig.getMaxConcurrentCalls();
        final ExecutorService executorService = Executors.newFixedThreadPool(numAttempts + 1);
        final List<Callable<String>> callables = new ArrayList<>();
        callables.add(() -> demoBulkhead.execute(() -> this.methodTakingSomeTime(sleepDurationForTheFirstCall), this::fallbackMethod));
        Stream.range(0, numAttempts)
                .forEach(count ->
                        callables.add(() ->
                                demoBulkhead.execute(() ->
                                        this.methodTakingSomeTime(LONGER_THAN_WAIT_DURATION), this::fallbackMethod))
                );
        final List<Future<String>> futures = executorService.invokeAll(callables);
        Assert.assertEquals(numAttempts + 1 - numExpectedFailures, futures.stream().filter(future -> {
            try {
                return future.get().equals(SUCCESSFUL_RESULT);
            } catch (final InterruptedException | ExecutionException e) {
                throw new AssertionError("the sleep call should not have been interrupted");
            }
        }).count());
        Assert.assertEquals(numExpectedFailures, futures.stream().filter(future -> {
            try {
                return future.get().equals(FALLBACK_RESULT);
            } catch (final InterruptedException | ExecutionException e) {
                throw new AssertionError("the sleep call should not have been interrupted");
            }
        }).count());
    }

    private String methodTakingSomeTime(final long durationInMillis) {
        try {
            Thread.sleep(durationInMillis);
        } catch (final InterruptedException ex) {
            throw new AssertionError("the sleep call should not have been interrupted");
        }
        return SUCCESSFUL_RESULT;
    }

    private String fallbackMethod(final Throwable exception) {
        return FALLBACK_RESULT;
    }
}
