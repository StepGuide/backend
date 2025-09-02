package com.stepguide.backend.domain.user.service;


import com.stepguide.backend.domain.user.dto.RefreshTokenDTO;
import com.stepguide.backend.domain.user.mapper.RefreshTokenMapper;
import com.stepguide.backend.global.response.BaseException;
import com.stepguide.backend.global.response.BaseResponseStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtService jwtService;

    // RT 발급 및 저장
    public void issueAndStoreRefreshToken(Long userId, HttpServletResponse res) {
        String rtValue = jwtService.createRefreshTokenValue();
        byte[] hash = HashUtil.sha256ToBytes(rtValue);

        RefreshTokenDTO entity = RefreshTokenDTO.builder()
                .userId(userId)
                .tokenHash(hash)
                .expiresAt(jwtService.calcRefreshExpiry())
                .revoked(false)
                .rotatedFrom(null)
                .build();

        refreshTokenMapper.insert(entity);
        jwtService.setRefreshCookie(res, rtValue);
    }

    // /api/auth/refresh: 회전(옛 RT 폐기 -> 새 RT 발급/저장 -> 새 Access 발급)
    @Transactional(rollbackFor = Exception.class)
    public String rotateAndIssueAccess(HttpServletRequest req, HttpServletResponse res) {
        String rtValue = jwtService.getRefreshTokenFromCookie(req);
        if (rtValue == null || rtValue.isBlank()) {
            throw new BaseException(BaseResponseStatus.AUTH_REFRESH_TOKEN_MISSING);
        }

        byte[] hash = HashUtil.sha256ToBytes(rtValue);
        RefreshTokenDTO stored = refreshTokenMapper.findByTokenHashForUpdate(hash);
        if (stored == null) {
            throw new BaseException(BaseResponseStatus.AUTH_REFRESH_TOKEN_INVALID);
        }
        if (stored.isRevoked()) {
            throw new BaseException(BaseResponseStatus.AUTH_REFRESH_TOKEN_REVOKED);
        }
        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BaseException(BaseResponseStatus.AUTH_REFRESH_TOKEN_EXPIRED);
        }

        // 조건부 폐기: revoked=false인 경우에만 폐기되도록
        int updated = refreshTokenMapper.revokeIfNotRevoked(stored.getId());
        if (updated != 1) {
            throw new BaseException(BaseResponseStatus.AUTH_REFRESH_TOKEN_REUSED);
        }

        // 새 RT 발급 & 저장
        String newRtValue = jwtService.createRefreshTokenValue();
        byte[] newHash = HashUtil.sha256ToBytes(newRtValue);

        RefreshTokenDTO rotated = RefreshTokenDTO.builder()
                .userId(stored.getUserId())
                .tokenHash(newHash)
                .expiresAt(jwtService.calcRefreshExpiry())
                .revoked(false)
                .rotatedFrom(stored.getId())
                .build();

        refreshTokenMapper.insert(rotated);
        jwtService.deferSetRefreshCookieAfterCommit(res, newRtValue);

        // 새 Access 토큰 발급
        return jwtService.generateAccessToken(String.valueOf(stored.getUserId()));
    }

    // /api/auth/logout: 현재 쿠키의 RT 폐기 + 쿠키 삭제
    public void logout(HttpServletRequest req, HttpServletResponse res) {
        String rtValue = jwtService.getRefreshTokenFromCookie(req);
        if (rtValue != null && !rtValue.isBlank()) {
            byte[] hash = HashUtil.sha256ToBytes(rtValue);
            RefreshTokenDTO stored = refreshTokenMapper.findByTokenHash(hash);
            if (stored != null) {
                refreshTokenMapper.revokeById(stored.getId());
            }
        }
        jwtService.clearRefreshCookie(res);
    }
}