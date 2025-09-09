package com.stepguide.backend.domain.FireBase.service;

import com.stepguide.backend.domain.FireBase.mapper.PushTokenMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PushTokenService {
    private final PushTokenMapper mapper;

    @Transactional
    public void register(Long userId, String token, String userAgent) {
        mapper.deactivateAllActiveByUserId(userId);
        mapper.upsert(userId, token, userAgent);
    }

    @Transactional
    public void unsubscribe(Long userId, String token) {
        mapper.unsubscribe(userId, token);
    }

    public Optional<String> findLatestActiveToken(Long userId) {
        return Optional.ofNullable(mapper.findLatestActiveToken(userId));
    }

    public int unsubscribeByToken(String token) {
        return mapper.unsubscribeByToken(token);
    }


}