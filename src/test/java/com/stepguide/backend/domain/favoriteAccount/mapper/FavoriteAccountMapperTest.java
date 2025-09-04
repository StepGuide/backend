package com.stepguide.backend.domain.favoriteAccount.mapper;

import com.stepguide.backend.domain.favoriteAccount.entity.FavoriteAccountVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FavoriteAccountMapperTest {

    @Autowired
    private FavoriteAccountMapper favoriteAccountMapper;

    @Test
    void addFavoriteAccount() {
        FavoriteAccountVO vo = new FavoriteAccountVO();
        vo.setUserId(3L);
        vo.setSendBankCode("005");
        vo.setSendAccountNumber("123-456-7890");
        vo.setSendBankNickname("손주");

        favoriteAccountMapper.addFavoriteAccount(vo);
    }

    @Test
    void getFavoriteAccount() {
        // Mapper 호출
        List<FavoriteAccountVO> favorites = favoriteAccountMapper.getFavoriteAccounts(1l);

        // 결과 출력
        for (FavoriteAccountVO vo : favorites) {
            System.out.println("ID: " + vo.getFavoriteId() +
                    ", UserID: " + vo.getUserId() +
                    ", BankCode: " + vo.getSendBankCode() +
                    ", AccountNumber: " + vo.getSendAccountNumber() +
                    ", Nickname: " + vo.getSendBankNickname());
        }
    }

    @Test
    public void delete(){
        favoriteAccountMapper.delete(12l);
    }
}
