package com.stepguide.backend.domain.fraudaccount.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FraudAccountServiceImplTest {
    @Autowired
    private FraudAccountService fraudAccountService;

    @Test
    @DisplayName("계좌 조회-사기계좌")
    void testCheckFraudAccount(){
        String testAccount = "100029496009";
        String result=fraudAccountService.checkFraudAccount(testAccount);
        System.out.println("조회 결과: "+result);
    }

    @Test
    @DisplayName("계좌 조회-사기계좌x")
    void testCheckFraudAccount2(){
        String testAccount = "200029496009";
        String result=fraudAccountService.checkFraudAccount(testAccount);
        System.out.println("조회 결과: "+result);
    }

}