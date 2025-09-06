package com.stepguide.backend.domain.accountTransfer.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PortOneClient {

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${portone.imp.key}")
    private String impKey;

    @Value("${portone.imp.secret}")
    private String impSecret;

    private static final String REDIS_TOKEN_KEY = "portone:access_token";

    public String getAccessToken() {
        // 1. Redis에서 먼저 확인
        Object cachedToken = redisTemplate.opsForValue().get(REDIS_TOKEN_KEY);
        if (cachedToken instanceof String token && !token.isEmpty()) {
            System.out.println("Redis 캐시 사용: " + token);
            return token;
        }

        // 2. 없으면 실제 발급 요청
        String url = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "imp_key", impKey,
                "imp_secret", impSecret
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("response")) {
            throw new IllegalStateException("PortOne 토큰 발급 실패");
        }

        Map<String, Object> resp = (Map<String, Object>) responseBody.get("response");
        String token = (String) resp.get("access_token");
        long expireAt = ((Number) resp.get("expired_at")).longValue();

        long now = Instant.now().getEpochSecond();
        long ttl = expireAt - now - 10; // 버퍼 10초

        // 3. Redis에 저장
        if (ttl > 0) {
            redisTemplate.opsForValue().set(REDIS_TOKEN_KEY, token, ttl, TimeUnit.SECONDS);
            System.out.println("Redis에 토큰 저장 완료 (TTL: " + ttl + "초)");
        }

        return token;
    }

    public String getAccountHolderName(String bankCode, String accountNumber) {
        String accessToken = getAccessToken();

        String url = String.format(
                "https://api.iamport.kr/vbanks/holder?bank_code=%s&bank_num=%s",
                bankCode, accountNumber
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("response")) {
            String errMsg = responseBody != null ? (String) responseBody.get("message") : "응답 없음";
            throw new IllegalArgumentException("PortOne 실명조회 실패: " + errMsg);
        }

        Map<String, Object> resp = (Map<String, Object>) responseBody.get("response");
        return (String) resp.get("bank_holder"); //예금주명 반환
    }

    /*
    토큰 강제 제거 후 발급 테스트 시 사용
     */
    public String refreshAccessToken() {
        // 1. 기존 토큰 제거
        redisTemplate.delete("portone:access_token");

        // 2. 새로 발급
        String url = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "imp_key", impKey,
                "imp_secret", impSecret
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null || !responseBody.containsKey("response")) {
            throw new IllegalStateException("PortOne 토큰 재발급 실패");
        }

        Map<String, Object> resp = (Map<String, Object>) responseBody.get("response");
        String token = (String) resp.get("access_token");
        long expireAt = ((Number) resp.get("expired_at")).longValue();

        long now = Instant.now().getEpochSecond();
        long ttl = expireAt - now - 10;

        if (ttl > 0) {
            redisTemplate.opsForValue().set("portone:access_token", token, ttl, TimeUnit.SECONDS);
            System.out.println("[refresh] Redis에 새 토큰 저장 (TTL: " + ttl + "s)");
        }

        return token;
    }
}
