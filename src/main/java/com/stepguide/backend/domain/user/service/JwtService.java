package com.stepguide.backend.domain.user.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.access-ttl-minutes}")
    private long accessTtlMinutes;

    @Value("${security.jwt.refresh-ttl-days}")
    private long refreshTtlDays;

    @Value("${security.jwt.refresh-cookie-name}")
    private String refreshCookieName;

    @Value("${security.jwt.refresh-cookie-domain}")
    private String refreshCookieDomain;

    @Value("${security.jwt.refresh-cookie-secure}")
    private boolean refreshCookieSecure;

    @Value("${security.jwt.refresh-cookie-samesite:Lax}")
    private String refreshCookieSameSite;

    private SecretKey getKey() {
        // HS256: 최소 256비트 이상의 시크릿 권장
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Access Token
    public String generateAccessToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + Duration.ofMinutes(accessTtlMinutes).toMillis());
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String parseSubject(String accessToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }

    // Refresh Token
    public String createRefreshTokenValue() {
        // 긴 랜덤 값
        return UUID.randomUUID().toString() + UUID.randomUUID();
    }

    // Refresh Cookie
    public void setRefreshCookie(HttpServletResponse res, String refreshTokenValue) {
        long maxAgeSeconds = Duration.ofDays(refreshTtlDays).toSeconds();

        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, refreshTokenValue)
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path("/")
                .domain(refreshCookieDomain)
                .maxAge(maxAgeSeconds)
                .sameSite(refreshCookieSameSite) // Lax 또는 None(운영에서 크로스도메인 시)
                .build();

        res.addHeader("Set-Cookie", cookie.toString());
    }

    public void clearRefreshCookie(HttpServletResponse res) {
        ResponseCookie cookie = ResponseCookie.from(refreshCookieName, "")
                .httpOnly(true)
                .secure(refreshCookieSecure)
                .path("/")
                .domain(refreshCookieDomain)
                .maxAge(0)
                .sameSite(refreshCookieSameSite)
                .build();

        res.addHeader("Set-Cookie", cookie.toString());
    }

    public String getRefreshTokenFromCookie(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (refreshCookieName.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    public LocalDateTime calcRefreshExpiry() {
        return LocalDateTime.now().plusDays(refreshTtlDays);
    }

    public void deferSetRefreshCookieAfterCommit(HttpServletResponse res, String rtValue) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() {
                    setRefreshCookie(res, rtValue);
                }
            });
        } else {
            setRefreshCookie(res, rtValue);
        }
    }
}