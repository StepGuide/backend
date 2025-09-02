package com.stepguide.backend.domain.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepguide.backend.domain.user.dto.KakaoTokenResponseDTO;
import com.stepguide.backend.domain.user.dto.KakaoUserDTO;
import com.stepguide.backend.global.response.BaseException;
import com.stepguide.backend.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoOAuthClient {

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.userinfo-uri}")
    private String userInfoUri;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

//    인가코드 -> 카카오 액세스 토큰 교환
    public KakaoTokenResponseDTO exchangeCodeForToken(String authorizationCode) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", clientId);
            form.add("redirect_uri", redirectUri);
            form.add("code", authorizationCode);

            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(form, headers);

            ResponseEntity<KakaoTokenResponseDTO> res =
                    restTemplate.postForEntity(tokenUri, req, KakaoTokenResponseDTO.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
            }
            return res.getBody();

        } catch (RestClientException e) {
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }


//     카카오 액세스 토큰으로 사용자 정보 조회
    public KakaoUserDTO fetchUser(String kakaoAccessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(kakaoAccessToken);
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> req = new HttpEntity<>(headers);

            ResponseEntity<String> res = restTemplate.exchange(
                    userInfoUri, HttpMethod.GET, req, String.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
            }

            JsonNode root = objectMapper.readTree(res.getBody());
            long kakaoId = root.path("id").asLong();

            JsonNode account = root.path("kakao_account");
            String email = account.path("email").isMissingNode() ? null : account.path("email").asText(null);

            String nickname = null;
            JsonNode profile = account.path("profile");
            if (!profile.isMissingNode()) {
                nickname = profile.path("nickname").asText(null);
            }

            return KakaoUserDTO.builder()
                    .kakaoId(kakaoId)
                    .email(email)
                    .nickname(nickname)
                    .build();

        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
}