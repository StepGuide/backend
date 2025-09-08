package com.stepguide.backend.domain.accountTransfer.external;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class PortOneClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock//실제 Redis 저장소 역할만 수행
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private PortOneClient portOneClient;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        // 테스트용 비밀키 세팅
        ReflectionTestUtils.setField(portOneClient, "impKey", "test-key");
        ReflectionTestUtils.setField(portOneClient, "impSecret", "test-secret");
    }

    @Test
    void 토큰이_없으면_API요청_후_Redis에_저장된다() {
        // given
        when(valueOperations.get("portone:access_token")).thenReturn(null);

        String token = "mock-token";
        long expireAt = Instant.now().getEpochSecond() + 1800;
        Map<String, Object> resp = Map.of("access_token", token, "expired_at", expireAt);
        Map<String, Object> body = Map.of("response", resp);
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(response);

        // when
        String result = portOneClient.getAccessToken();

        // then
        assertEquals(token, result);
        verify(valueOperations).set(eq("portone:access_token"), eq(token), anyLong(), eq(TimeUnit.SECONDS));
    }

    @Test
    void 토큰이_Redis에_있으면_API요청없이_그대로_사용된다() {
        // given
        when(valueOperations.get("portone:access_token")).thenReturn("cached-token");

        // when
        String result = portOneClient.getAccessToken();

        // then
        assertEquals("cached-token", result);
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(Map.class));
    }

    @Test
    void 토큰이_만료되었으면_다시_발급되고_Redis에_저장된다() {
        // given
        when(valueOperations.get("portone:access_token")).thenReturn(null); // Redis에 없음

        String newToken = "new-token";
        long expireAt = Instant.now().getEpochSecond() + 60; // 1분 남은 만료
        Map<String, Object> resp = Map.of("access_token", newToken, "expired_at", expireAt);
        Map<String, Object> body = Map.of("response", resp);
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(response);

        // when
        String result = portOneClient.getAccessToken();

        // then
        assertEquals(newToken, result);
        verify(valueOperations).set(eq("portone:access_token"), eq(newToken), anyLong(), eq(TimeUnit.SECONDS));
    }
    @Test
    void refreshAccessToken_기존토큰삭제_새토큰발급_저장된다() {
        String refreshedToken = "refreshed-token";
        long expireAt = Instant.now().getEpochSecond() + 1200;
        Map<String, Object> resp = Map.of("access_token", refreshedToken, "expired_at", expireAt);
        Map<String, Object> body = Map.of("response", resp);
        ResponseEntity<Map> response = new ResponseEntity<>(body, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
                .thenReturn(response);

        String result = portOneClient.refreshAccessToken();

        assertEquals(refreshedToken, result);
        verify(redisTemplate).delete("portone:access_token");
        verify(valueOperations).set(eq("portone:access_token"), eq(refreshedToken), anyLong(), eq(TimeUnit.SECONDS));

        //추가: 유효시간 TTL 확인 출력
        long now = Instant.now().getEpochSecond();
        long ttl = expireAt - now;
        System.out.println("portone 토큰 TTL (초) = " + ttl);
    }
}
