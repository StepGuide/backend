package com.stepguide.backend.domain.favoriteAccount.entity;

import lombok.*;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteAccountVO {
    private Long favoriteId;          // 즐겨찾기 ID (PK)
    private Long userId;              // 회원 ID (FK)
    private String sendBankCode;      // 상대 은행 코드
    private String sendAccountNumber; // 상대 은행 계좌번호
    private String sendBankNickname; // 상대 은행 별명
    private LocalDateTime createdAt;  // 생성일자
    private LocalDateTime updatedAt;  // 수정일
}
