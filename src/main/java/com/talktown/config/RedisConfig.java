package com.talktown.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    @Value("${REDIS_HOST}")
    private String redisHost;

    @Value("${REDIS_PORT}")
    private int redisPort;

    @Value("${REDIS_PASSWORD}")
    private String redisPassword;

    @Bean
    public Jedis jedisClient() {
        logger.info("Connecting to Redis at {}:{}", redisHost, redisPort);
        try {
            Jedis jedis = new Jedis(
                    "redis://default:".concat(redisPassword).concat("@")
                            .concat(redisHost).concat(":").concat(String.valueOf(redisPort))
            );
            logger.info("---- Redis connection successful ----");
            return jedis;
        } catch (Exception e) {
            logger.error("Failed to connect to Redis: {}", e.getMessage());
            throw e;
        }
    }
}
