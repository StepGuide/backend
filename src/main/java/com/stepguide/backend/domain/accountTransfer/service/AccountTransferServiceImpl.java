package com.stepguide.backend.domain.accountTransfer.service;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import com.stepguide.backend.domain.accountTransfer.external.PortOneClient;
import com.stepguide.backend.domain.accountTransfer.mapper.AccountTransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountTransferServiceImpl implements AccountTransferService{

    private final AccountTransferMapper accountTransferMapper;
    private final PortOneClient portOneClient;
//    private final AccountTransferService accountTransferService;

//    public AccountTransferServiceImpl(AccountTransferMapper mapper){
//        this.accountTransferMapper = mapper;
//    }

    @Override
//    @Transactional
    public void transfer(AccountTransferDTO dto){
        // 1. 출금 계좌 조회
        AccountTransferVO fromAccount = accountTransferMapper.findAccountByAccountNumber(
          dto.getAccountNumber(), dto.getBankCode()
        );

        if(fromAccount == null){
            throw new IllegalArgumentException("출금 계좌를 찾을 수 없습니다.");
        }

        // 2. 잔액 확인
        if(fromAccount.getBalance().compareTo(dto.getTransactionAmount())<0){
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 3. 출금 처리 (잔액 차감)
        BigDecimal newFromBalance = fromAccount.getBalance().subtract(dto.getTransactionAmount());
        accountTransferMapper.updateAccountBalance(fromAccount.getAccountId(), newFromBalance);

        // 4. 입금 계좌 조회
//        AccountTransferVO toAccount = accountTransferMapper.findAccountByAccountNumber(
//                dto.getPayeeAccountNumber(), dto.getSendBankCode()
//        );
//
//        if(toAccount == null){
//            throw new IllegalArgumentException("입금 계좌가 존재하지 않습니다.");
//        }

        // 4. 입금 계좌 존재 여부 + 예금주 확인
//        String token = portOneClient.getAccessToken(impKey, impSecret);
        String accountHolderName = portOneClient.getAccountHolderName(
                dto.getSendBankCode(),
                dto.getPayeeAccountNumber()
//                token
        );

        if (accountHolderName == null || accountHolderName.isBlank()) {
            throw new IllegalArgumentException("입금 계좌가 존재하지 않습니다.");
        }

        // 5. 거래 내역 기록 시 예금주명도 저장
//        dto.setAccountHolderName(accountHolderName);

        // 5. 거래 내역 기록
        dto.setAccountId(fromAccount.getAccountId());       //출금계좌id
        dto.setDepositWithdrawal(AccountTransferDTO.DepositWithdrawal.WITHDRAWAL);  //출금
        dto.setStatus("SUCCESS");                           //거래상태
        dto.setCreatedTime(java.time.LocalDateTime.now());  //거래시각
        dto.setAccountHolderName(accountHolderName);        //예금주명
        accountTransferMapper.insertTransaction(dto);

    }

}
