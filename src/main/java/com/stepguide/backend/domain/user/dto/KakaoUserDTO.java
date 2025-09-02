package com.stepguide.backend.domain.user.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoUserDTO {
    private Long kakaoId;
    private String email;
    private String nickname;
}