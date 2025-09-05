package com.stepguide.backend.domain.favoriteAccount.dto;

import com.stepguide.backend.domain.favoriteAccount.entity.FavoriteAccountVO;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteAccountDTO {
    private Long favoriteId;
    private Long userId;
    private String sendBankCode; // 송금 은행 코드 (프런트 입력)
    private String sendAccountNumber; // 송금 계좌번호 (프런트 입력)
    private String sendBankNickname; // 즐겨찾기 이름 (프런트 입력)

    //VO-> DTO로 변환
    public static FavoriteAccountDTO of(FavoriteAccountVO vo){
        return vo == null?null:FavoriteAccountDTO.builder()
                .favoriteId(vo.getFavoriteId())
                .userId(vo.getUserId())
                .sendBankCode(vo.getSendBankCode())
                .sendAccountNumber(vo.getSendAccountNumber())
                .sendBankNickname(vo.getSendBankNickname())
                .build();
    }

    //VO-> DTO로 변환
    public FavoriteAccountVO toVo(){
    return FavoriteAccountVO.builder()
            .favoriteId(favoriteId)
            .userId(userId)
            .sendBankCode(sendBankCode)
            .sendAccountNumber(sendAccountNumber)
            .sendBankNickname(sendBankNickname)
            .build();
    }

}
