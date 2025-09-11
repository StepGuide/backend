package com.stepguide.backend.domain.accountTransfer.service;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountTransferService {

    // 내 전체 계좌 조회
    List<AccountTransferDTO> getUserAccounts(Long userId);

    // 특정 계좌 내역 조회
    List<AccountTransferDTO> getTransactionByAccountId(Long accountId);

    //거래내역 조회
    List<AccountTransferDTO> getAccountTransactions(Long accountId);

    // 검증 (예금주명, 잔액 등 확인)
    AccountTransferDTO validateTransfer(AccountTransferDTO dto);

    // 지연 or 즉시 선택
    void executeTransfer(AccountTransferDTO dto);

    //즉시이체(잔액 차감 + 거래 내역 저장)
    void processImmediateTransfer(AccountTransferDTO dto);

    //지연이쳬 예약
    void scheduledDelayedTransfer(AccountTransferDTO dto);

    //지연이체 실행
    void processDelayedTransfer(AccountTransferDTO dto);

    //테이블 젤위 계좌 조회
    AccountTransferDTO getFirstAccountTransfer(Long userId);

}
