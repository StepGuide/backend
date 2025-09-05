package com.stepguide.backend.domain.user.service;

import com.stepguide.backend.domain.user.dto.KakaoTokenResponseDTO;
import com.stepguide.backend.domain.user.dto.KakaoUserDTO;
import com.stepguide.backend.global.response.BaseException;
import com.stepguide.backend.global.response.BaseResponseStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;         // 외부 API 호출
    private final UserService userService;                   // users upsert
    private final RefreshTokenService refreshTokenService;   // RT 발급/쿠키
    private final JwtService jwtService;                     // Access 발급

    /**
      1) 인가코드 -> 카카오 access_token 교환
      2) access_token -> 카카오 사용자 조회
      3) 우리 DB upsert -> userId
      4) RT 발급/저장/쿠키 + Access 토큰 발급 -> access 반환
     */
    public String loginWithAuthorizationCode(String authorizationCode, HttpServletResponse res) {
        if (authorizationCode == null || authorizationCode.isBlank()) {
            throw new BaseException(BaseResponseStatus.AUTH_AUTHORIZATION_CODE_MISSING);
        }

        KakaoTokenResponseDTO tokenRes = kakaoOAuthClient.exchangeCodeForToken(authorizationCode);
        String kakaoAccessToken = tokenRes.getAccessToken();
        if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
            throw new BaseException(BaseResponseStatus.AUTH_KAKAO_ACCESS_TOKEN_MISSING);
        }

        KakaoUserDTO kakaoUser = kakaoOAuthClient.fetchUser(kakaoAccessToken);
        Long userId = userService.loginOrRegisterKakao(kakaoUser);

        // RT 발급/저장/쿠키
        refreshTokenService.issueAndStoreRefreshToken(userId, res);

        // Access 토큰 발급
        return jwtService.generateAccessToken(String.valueOf(userId));
    }


//      이미 카카오 access_token 을 가진 경우 바로 유저 조회
    public String loginWithKakaoAccessToken(String kakaoAccessToken, HttpServletResponse res) {
        if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
            throw new BaseException(BaseResponseStatus.AUTH_KAKAO_ACCESS_TOKEN_MISSING);
        }

        KakaoUserDTO kakaoUser = kakaoOAuthClient.fetchUser(kakaoAccessToken);
        Long userId = userService.loginOrRegisterKakao(kakaoUser);

        refreshTokenService.issueAndStoreRefreshToken(userId, res);
        return jwtService.generateAccessToken(String.valueOf(userId));
    }
}