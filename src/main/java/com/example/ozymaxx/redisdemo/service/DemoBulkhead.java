package com.example.ozymaxx.redisdemo.service;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class DemoBulkhead {
    private static final String BACKEND_B_NAME = "backendB";

    private Bulkhead bulkhead;

    public DemoBulkhead(final BulkheadRegistry bulkheadRegistry) {
        this.bulkhead = bulkheadRegistry.bulkhead(BACKEND_B_NAME);
    }

    public Bulkhead getBulkhead() {
        return this.bulkhead;
    }

    public <T> T execute(final Supplier<T> actualFunction, final Function<Throwable, T> fallbackMethod) {
        final Supplier<T> decoratedCall = Bulkhead.decorateSupplier(bulkhead, actualFunction);
        return Try.of(decoratedCall::get).recover(fallbackMethod).get();
    }
}
