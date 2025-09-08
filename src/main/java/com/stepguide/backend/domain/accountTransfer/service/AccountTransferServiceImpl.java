package com.stepguide.backend.domain.accountTransfer.service;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import com.stepguide.backend.domain.accountTransfer.external.PortOneClient;
import com.stepguide.backend.domain.accountTransfer.mapper.AccountTransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class  AccountTransferServiceImpl implements AccountTransferService{

    private final AccountTransferMapper accountTransferMapper;
    private final PortOneClient portOneClient;

    // 내 전체 계좌 조회
    @Override
    public List<AccountTransferDTO> getUserAccounts(Long userId){
        List<AccountTransferVO> accounts = accountTransferMapper.findAccountsByUserId(userId);
        return accounts.stream()
                .map(AccountTransferDTO::of)
                .collect(Collectors.toList());
    }

    //거래내역 조회
    @Transactional
    public List<AccountTransferDTO> getAccountTransactions(Long accountId){
        List<AccountTransferVO> accounts = accountTransferMapper.findAccountTransactions(accountId);
        return accounts.stream()
                .map(AccountTransferDTO::of)
                .collect(Collectors.toList());
    }

    // 검증 (예금주명, 잔액 등 확인)
    @Override
    public AccountTransferDTO validateTransfer(AccountTransferDTO dto) {
        // 출금 계좌 조회
        AccountTransferVO fromAccount = accountTransferMapper.findAccountByAccountNumber(
                dto.getAccountNumber(), dto.getBankCode());

        if (fromAccount == null) {
            throw new IllegalArgumentException("출금 계좌가 존재하지 않습니다.");
        }

        if (fromAccount.getBalance().compareTo(dto.getTransactionAmount()) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 예금주명 조회 (PortOne API)
        String accountHolderName = portOneClient.getAccountHolderName(
                dto.getSendBankCode(),
                dto.getPayeeAccountNumber()
        );

        if (accountHolderName == null || accountHolderName.isBlank()) {
            throw new IllegalArgumentException("입금 계좌가 존재하지 않습니다.");
        }

        // 사용자 확인을 위해 DTO에 세팅
        dto.setAccountHolderName(accountHolderName);
        dto.setAccountId(fromAccount.getAccountId());
        return dto;
    }

    // 이체  (잔액 차감 + 거래 내역 저장)
    @Override
//    @Transactional
    public void executeTransfer(AccountTransferDTO dto) {
        // 출금 계좌 조회
        AccountTransferVO fromAccount = accountTransferMapper.findAccountByAccountNumber(
                dto.getAccountNumber(), dto.getBankCode());

        if (fromAccount == null) {
            throw new IllegalArgumentException("출금 계좌가 존재하지 않습니다.");
        }

        // 잔액 차감
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(dto.getTransactionAmount());
        accountTransferMapper.updateAccountBalance(fromAccount.getAccountId(), newFromBalance);

        // 거래 내역 기록
        dto.setDepositWithdrawal(AccountTransferDTO.DepositWithdrawal.WITHDRAWAL);
        dto.setStatus("SUCCESS");
        dto.setCreatedTime(LocalDateTime.now());

        accountTransferMapper.insertTransactions(dto);
    }
}
