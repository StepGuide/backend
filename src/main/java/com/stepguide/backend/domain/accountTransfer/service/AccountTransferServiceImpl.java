package com.stepguide.backend.domain.accountTransfer.service;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import com.stepguide.backend.domain.accountTransfer.external.PortOneClient;
import com.stepguide.backend.domain.accountTransfer.mapper.AccountTransferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
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

    // 이체 실행
    @Override
    public void executeTransfer(AccountTransferDTO dto) {
        if(dto.getTransferType()==AccountTransferDTO.TransferType.IMMEDIATE){
            processImmediateTransfer(dto);
        }else if(dto.getTransferType()==AccountTransferDTO.TransferType.DELAYED){
            scheduledDelayedTransfer(dto);
        }
    }
    //즉시 이체
    @Transactional
    @Override
    public void processImmediateTransfer(AccountTransferDTO dto){
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

    //지연 이체 예약
    @Override
    public void scheduledDelayedTransfer(AccountTransferDTO dto){
        //지연 거래 내역 기록
        LocalDateTime now=LocalDateTime.now();
        dto.setDepositWithdrawal(AccountTransferDTO.DepositWithdrawal.WITHDRAWAL);
        dto.setStatus("PENDING");
        dto.setCreatedTime(now);
        dto.setScheduledTime(now.plusMinutes(10)); //나중에 고려

        accountTransferMapper.insertDelayedTransactions(dto);

    }

    //지연 이체 처리 (PENDING->SUCCESS)
    @Override
    @Transactional
    public void processDelayedTransfer(AccountTransferDTO dto){
        AccountTransferDTO validated_dto=validateTransfer(dto); //다시 한번 유효성 검증(출금계좌 존재여부, 잔액, 예금주명)
        processImmediateTransfer(validated_dto); //transactions 기록 + 잔액 차감
        log.info("지연이체 실행: " + validated_dto);
        accountTransferMapper.updateDelayedTransactionStatus(validated_dto.getDealyedTransactionId(), "SUCCESS");
    }



}
