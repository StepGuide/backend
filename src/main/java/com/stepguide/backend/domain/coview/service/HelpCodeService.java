package com.stepguide.backend.domain.coview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class HelpCodeService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveHelpCode(String code, String userId) {
        redisTemplate.opsForValue().set("help-code:" + code, userId, Duration.ofMinutes(10));
    }

    public String getUserIdByCode(String code) {
        return redisTemplate.opsForValue().get("help-code:" + code);
    }

    public void deleteCode(String code) {
        redisTemplate.delete("help-code:" + code);
    }
}
