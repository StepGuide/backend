package com.stepguide.backend.domain.user.controller;

import com.stepguide.backend.global.response.BaseResponse;
import com.stepguide.backend.global.response.BaseResponseStatus;
import com.stepguide.backend.domain.user.service.JwtService;
import com.stepguide.backend.domain.user.service.RefreshTokenService;
import com.stepguide.backend.domain.user.service.KakaoOAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final KakaoOAuthService kakaoOAuthService;

    // 로그인
    @PostMapping("/login/kakao")
    public BaseResponse<LoginResponse> loginKakao(
            @RequestBody KakaoLoginRequest body,
            HttpServletResponse res
    ) {
        if (body == null) {
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST);
        }

        String access;
        if (body.getCode() != null && !body.getCode().isBlank()) {
//            인가코드로 조회
            access = kakaoOAuthService.loginWithAuthorizationCode(body.getCode(), res);
        } else if (body.getKakaoAccessToken() != null && !body.getKakaoAccessToken().isBlank()) {
//            엑세스 토큰이 존재 시 바로 토큰으로 조회
            access = kakaoOAuthService.loginWithKakaoAccessToken(body.getKakaoAccessToken(), res);
        } else {
            // 둘 다 없으면 400
            return new BaseResponse<>(BaseResponseStatus.BAD_REQUEST_INVALID_PARAM);
        }
        return new BaseResponse<>(new LoginResponse(access));
    }

//     리프레시 토큰 회전
    @PostMapping("/refresh")
    public BaseResponse<AccessTokenResponse> refresh(
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        String newAccess = refreshTokenService.rotateAndIssueAccess(req, res);
        return new BaseResponse<>(new AccessTokenResponse(newAccess));
    }

//  로그아웃
    @PostMapping("/logout")
    public BaseResponse<Void> logout(
            HttpServletRequest req,
            HttpServletResponse res
    ) {
        refreshTokenService.logout(req, res);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @Getter @Setter
    public static class DevLoginRequest {
        private Long userId;
    }

    @Getter @Setter
    public static class KakaoLoginRequest {
        private String code;
        private String kakaoAccessToken;
    }

    @Getter @AllArgsConstructor
    public static class LoginResponse {
        private String accessToken;
    }

    @Getter @AllArgsConstructor
    public static class AccessTokenResponse {
        private String accessToken;
    }
}