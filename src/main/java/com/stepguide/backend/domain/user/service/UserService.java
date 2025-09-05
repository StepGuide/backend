package com.stepguide.backend.domain.user.service;

import com.stepguide.backend.domain.user.dto.KakaoUserDTO;
import com.stepguide.backend.domain.user.dto.UserDTO;
import com.stepguide.backend.domain.user.mapper.UserMapper;
import com.stepguide.backend.global.response.BaseException;
import com.stepguide.backend.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.log4j.Log4j2;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserMapper userMapper;

    /**
     * 카카오에서 받아온 사용자 정보를 우리 DB에 userId를 반환.
     * - 존재: provider="kakao", provider_id=카카오ID 로 조회 있으면 user_id 반환
     * - 존재 X: NOT NULL 컬럼을 더미 포함해 INSERT 후 user_id 반환
     */
    public Long loginOrRegisterKakao(KakaoUserDTO kakaoUser) {
        if (kakaoUser == null || kakaoUser.getKakaoId() == null) {
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        }

        String provider = "kakao";
        String providerId = String.valueOf(kakaoUser.getKakaoId());

        // 존재 여부 확인
        Long existingId = userMapper.findIdByProviderAndProviderId(provider, providerId);
        if (existingId != null) {
            return existingId;
        }

        // id 존재하지 않으면 DB에 insert
        String username = kakaoUser.getNickname() != null ? kakaoUser.getNickname() : "카카오사용자";
        String email = kakaoUser.getEmail() != null
                ? kakaoUser.getEmail()
                : ("kakao_" + providerId + "@gmail.com"); // 이메일 동의 없을 때 더미

        UserDTO newUser = UserDTO.builder()
                .username(username)
                .email(email)
                .phoneNumber("010-1234-5678")        // 더미
                .birthDate(LocalDate.of(1970, 1, 1)) // 더미
                .provider(provider)
                .providerId(providerId)
                .build();

        try {
            userMapper.insertUser(newUser);
            if (newUser.getUserId() == null) {
                Long id = userMapper.findIdByProviderAndProviderId(provider, providerId);
                if (id == null) {
                    throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
                }
                return id;
            }
            return newUser.getUserId();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("DataIntegrityViolationException: {}", e.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_CONSTRAINT_ERROR);
        } catch (Exception e) {
            log.error("UserService insert error", e);
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}