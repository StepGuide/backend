package com.stepguide.backend.domain.accountTransfer.service;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccountTransferService {

    // 내 전체 계좌 조회
    List<AccountTransferDTO> getUserAccounts(Long userId);

    //거래내역 조회
    List<AccountTransferDTO> getAccountTransactions(Long accountId);

    // 검증 (예금주명, 잔액 등 확인)
    AccountTransferDTO validateTransfer(AccountTransferDTO dto);


    // 이체  (잔액 차감 + 거래 내역 저장)
    void executeTransfer(AccountTransferDTO dto);


}
