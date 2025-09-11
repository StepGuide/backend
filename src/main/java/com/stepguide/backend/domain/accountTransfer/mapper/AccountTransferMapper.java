package com.stepguide.backend.domain.accountTransfer.mapper;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AccountTransferMapper {

    // 내 전체 계좌 조회
    List<AccountTransferVO> findAccountsByUserId(@Param("userId") Long userId);

    // 특정 계좌 조회
        List<AccountTransferVO> findTransactionsByAccountId(@Param("accountId") Long accountId);


    // 출금 계좌 조회
    AccountTransferVO findAccountByAccountNumber(@Param("accountNumber") String accountNumber,
                                                 @Param("bankCode") String bankCode);

    // 출금 게좌 잔액 업데이트
    void updateAccountBalance(@Param("accountId") Long accountId,
                              @Param("balance") BigDecimal balance);

    // 거래 내역 저장
    void insertTransactions(AccountTransferDTO dto);

    // 거래 내역 조회
    List<AccountTransferVO> findAccountTransactions(@Param("accountId")Long accountId);

    //지연 거래 내역 저장
    void insertDelayedTransactions(AccountTransferDTO dto);

    //지연 거래 내역 status 갱신
    void updateDelayedTransactionStatus(@Param("delayedTransactionId") Long delayedTransactionId, @Param("status") String status);

    //준비된 지연 거래 내역 찾기
    List<AccountTransferDTO> findDelayedTransactionsReady(@Param("now") LocalDateTime now);

    //테이블 젤 위 계좌 조회
    AccountTransferDTO findAccountOneByUserId (@Param("userId") Long userId);

}
