package com.stepguide.backend.domain.accountTransfer.service;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import com.stepguide.backend.domain.accountTransfer.mapper.AccountTransferMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AccountTransferServiceImplTest {
    @Autowired
    private AccountTransferMapper accountTransferMapper;


    @Autowired
    private AccountTransferServiceImpl accountTransferServiceImpl;


    @Test
    void processImmediateTransfer() {
        // 1️⃣ 테스트용 계좌 정보
        String accountNumber = "222-222-2222";
        String bankCode = "002";

        AccountTransferVO fromAccountBefore = accountTransferMapper.findAccountByAccountNumber(accountNumber, bankCode);
        assertNotNull(fromAccountBefore, "출금 계좌가 존재해야 합니다.11");

        BigDecimal initialBalance = fromAccountBefore.getBalance();
        BigDecimal transferAmount = BigDecimal.valueOf(121212);

        // 2️⃣ DTO 생성
        AccountTransferDTO dto = new AccountTransferDTO();
        dto.setAccountId(fromAccountBefore.getAccountId());
        dto.setAccountNumber(accountNumber);
        dto.setBankCode(bankCode);
        dto.setTransactionAmount(transferAmount);
        dto.setPayeeAccountNumber("222-222-2222");
        dto.setAccountHolderName("테스트 수취인1");
        dto.setSendBankCode("002");
        dto.setMemo("테스트 이체");

        // 3️⃣ 이체 실행
        accountTransferServiceImpl.processImmediateTransfer(dto);

        // 4️⃣ 결과 확인
        AccountTransferVO fromAccountAfter = accountTransferMapper.findAccountByAccountNumber(accountNumber, bankCode);
        assertEquals(initialBalance.subtract(transferAmount), fromAccountAfter.getBalance(), "잔액이 정상적으로 차감되어야 합니다.");

        // 거래 내역 확인
        List<AccountTransferVO> transactions = accountTransferMapper.findAccountTransactions(fromAccountAfter.getAccountId());
        System.out.println(transactions);
        boolean transactionRecorded = transactions.stream().anyMatch(tx ->
                tx.getTransactionAmount().compareTo(transferAmount) == 0 &&
                        tx.getDepositWithdrawal() == AccountTransferDTO.DepositWithdrawal.WITHDRAWAL &&
                        "테스트 이체".equals(tx.getMemo() != null ? tx.getMemo().trim() : null)
        );

        assertTrue(transactionRecorded, "거래 내역이 정상적으로 기록되어야 합니다.");


    }

    @Test
    void scheduledDelayedTransfer() {
        // 1️⃣ 테스트용 계좌 정보
        String accountNumber = "222-222-2222";
        String bankCode = "002";

        AccountTransferVO fromAccount = accountTransferMapper.findAccountByAccountNumber(accountNumber, bankCode);
        assertNotNull(fromAccount, "출금 계좌가 존재해야 합니다.");

        // 2️⃣ DTO 생성
        AccountTransferDTO dto = new AccountTransferDTO();
        dto.setAccountId(fromAccount.getAccountId());
        dto.setAccountNumber(accountNumber);
        dto.setBankCode(bankCode);
        dto.setTransactionAmount(BigDecimal.valueOf(500));
        dto.setPayeeAccountNumber("1234");
        dto.setAccountHolderName("테스트 수취인2");
        dto.setSendBankCode("003");
        dto.setMemo("지연 테스트 이체");

        // 3️⃣ 지연 이체 예약 실행
        accountTransferServiceImpl.scheduledDelayedTransfer(dto);

    }


    @Test
    void executeTransfer1() {
        // 1️⃣ 테스트용 계좌 정보
        String accountNumber = "222-222-2222";
        String bankCode = "002";

        AccountTransferVO fromAccount = accountTransferMapper.findAccountByAccountNumber(accountNumber, bankCode);
        assertNotNull(fromAccount, "출금 계좌가 존재해야 합니다.");

        // 2️⃣ DTO 생성
        AccountTransferDTO dto = new AccountTransferDTO();
        dto.setAccountId(fromAccount.getAccountId());
        dto.setAccountNumber(accountNumber);
        dto.setBankCode(bankCode);
        dto.setTransactionAmount(BigDecimal.valueOf(500));
        dto.setPayeeAccountNumber("85470201250674");
        dto.setAccountHolderName("테스트 수취인3");
        dto.setSendBankCode("004");
        dto.setMemo("executeTransfer 테스트");

        dto.setTransferType(AccountTransferDTO.TransferType.IMMEDIATE);

        accountTransferServiceImpl.executeTransfer(dto);

    }

    @Test
    void executeTransfer2() {
        // 1️⃣ 테스트용 계좌 정보
        String accountNumber = "222-222-2222";
        String bankCode = "002";

        AccountTransferVO fromAccount = accountTransferMapper.findAccountByAccountNumber(accountNumber, bankCode);
        assertNotNull(fromAccount, "출금 계좌가 존재해야 합니다.");

        // 2️⃣ DTO 생성
        AccountTransferDTO dto = new AccountTransferDTO();
        dto.setAccountId(fromAccount.getAccountId());
        dto.setAccountNumber(accountNumber);
        dto.setBankCode(bankCode);
        dto.setTransactionAmount(BigDecimal.valueOf(500));
        dto.setPayeeAccountNumber("85470201250674");
        dto.setAccountHolderName("테스트 수취인4");
        dto.setSendBankCode("004");
        dto.setMemo("executeTransfer 테스트2");

        dto.setTransferType(AccountTransferDTO.TransferType.DELAYED);

        accountTransferServiceImpl.executeTransfer(dto);

    }

}