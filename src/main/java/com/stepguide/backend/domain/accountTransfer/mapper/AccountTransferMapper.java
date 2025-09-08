package com.stepguide.backend.domain.accountTransfer.mapper;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountTransferMapper {

    // 내 전체 계좌 조회
    List<AccountTransferVO> findAccountsByUserId(@Param("userId") Long userId);

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

}
