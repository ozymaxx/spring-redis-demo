package com.example.ozymaxx.redisdemo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    public TestRedisConfiguration(
            @Value("${spring.cache.redis.port}") final int port) {
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void launchRedisServer() {
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedisServer() {
        this.redisServer.stop();
    }
}
