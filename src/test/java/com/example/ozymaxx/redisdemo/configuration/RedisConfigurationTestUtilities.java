package com.example.ozymaxx.redisdemo.configuration;

import redis.embedded.RedisServer;

public class RedisConfigurationTestUtilities {

    private RedisServer redisServer;

    public RedisConfigurationTestUtilities(int redisServerPort) {
        this.redisServer = new RedisServer(redisServerPort);
    }

    public void launchRedisServer() {
        this.redisServer.start();
    }

    public void stopRedisServer() {
        this.redisServer.stop();
    }
}
