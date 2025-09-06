package com.stepguide.backend.domain.accountTransfer.mapper;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@MapperScan("com.stepguide.backend.domain.accountTransfer.mapper")
@Transactional
public class AccountTransferMapperTest {

    @Autowired
    private AccountTransferMapper mapper;

    // 1. 출금 계좌 조회
    @Test
    public void testFindAccountByAccountNumber(){
        String accountNumber = "1234567890";
        String bankCode = "0001";

        AccountTransferVO account = mapper.findAccountByAccountNumber(accountNumber, bankCode);

        assertNotNull(account); // 계좌가 존재하는지
        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals(bankCode, account.getBankCode());
        System.out.println("조회 계좌: " + account);
    }

    // 2. 입금/출금 잔액 업데이트
    @Test
    public void testUpdateAccountBalance() {
        Long accountId = 1L;
        BigDecimal newBalance = new BigDecimal("500000.00");

        int updated = mapper.updateAccountBalance(accountId, newBalance);

        assertEquals(1, updated); // 업데이트 성공 여부
        AccountTransferVO account = mapper.findAccountByAccountNumber("1234567890", "0001");
        assertEquals(newBalance, account.getBalance());
        System.out.println("업데이트 후 잔액: " + account.getBalance());
    }

    // 3. 거래내역 기록
    @Test
    public void testInsertTransaction() {
        AccountTransferDTO dto = AccountTransferDTO.builder()
                .accountId(1L)
                .status("SUCCESS")
                .sendBankCode("0002")
                .transactionAmount(new BigDecimal("10000.00"))
                .createdTime(LocalDateTime.now())
                .depositWithdrawal(AccountTransferDTO.DepositWithdrawal.WITHDRAWAL)
                .payeeAccountNumber("9876543210")
                .build();

        int inserted = mapper.insertTransaction(dto);
        assertEquals(1, inserted); // 1개 행 삽입 확인
    }


}