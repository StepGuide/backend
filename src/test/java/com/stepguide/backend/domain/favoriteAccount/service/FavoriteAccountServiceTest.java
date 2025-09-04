package com.stepguide.backend.domain.favoriteAccount.service;

import com.stepguide.backend.domain.favoriteAccount.dto.FavoriteAccountDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FavoriteAccountServiceTest {

    @Autowired
    private FavoriteAccountService service;

    @Test
    void getFavoriteAccountList() {
        long userId = 1l;
        for(FavoriteAccountDTO favoriteAccount : service.getFavoriteAccountList(userId)) {
            System.out.println(favoriteAccount);
        }
    }

    @Test
    void addFavoriteAccount() {
        FavoriteAccountDTO favorite = new FavoriteAccountDTO();
        favorite.setUserId(1l);
        favorite.setSendAccountNumber("1234-5678-9012");
        favorite.setSendBankCode("0123");
        favorite.setSendBankNickname("최준혁");

        service.addFavoriteAccount(favorite);
    }

    @Test
    void delete(){
        service.delete(11l);
    }
}