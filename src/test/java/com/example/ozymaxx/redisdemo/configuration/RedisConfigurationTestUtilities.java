package com.example.ozymaxx.redisdemo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class RedisConfigurationTestUtilities {

    private RedisServer redisServer;

    public RedisConfigurationTestUtilities(@Value("${spring.cache.redis.port}") int redisServerPort) {
        this.redisServer = new RedisServer(redisServerPort);
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