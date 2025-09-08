package com.stepguide.backend.domain.coview;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisTestRunner implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(String... args) {
        redisTemplate.opsForValue().set("testKey", "Hello Redis!");
        String value = (String) redisTemplate.opsForValue().get("testKey");
        System.out.println("✅ Redis에서 가져온 값: " + value);
    }
}
