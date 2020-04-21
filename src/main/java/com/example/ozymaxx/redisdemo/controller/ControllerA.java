package com.example.ozymaxx.redisdemo.controller;

import com.example.ozymaxx.redisdemo.service.BackendA;
import com.example.ozymaxx.redisdemo.service.dto.ResponseA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backendA")
public class ControllerA {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerA.class);

    private final BackendA backendA;

    public ControllerA(final BackendA backendA) {
        this.backendA = backendA;
    }

    @CacheEvict(value = "sample-redis-cache", allEntries = true)
    public void evictCache() {
        LOG.debug("all entries have been evicted");
    }

    @GetMapping(value = "/{value}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Cacheable(value = "sample-redis-cache", key = "#value")
    public ResponseA backendA(@PathVariable final String value) {
        LOG.debug("no entry found for {}", value);
        return backendA.method(value);
    }
}
