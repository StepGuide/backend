package com.stepguide.backend.domain.accountTransfer.mapper;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccountTransferMapper {

//    //1. 출금 계좌 조회
//    public AccountTransferVO findAccountByAccountNumber(
//            @Param("accountNumber") String accountNumber,
//            @Param("bankCode") String bankcode
//    );
//
//    //2. 출금/입금 잔액 업데이트
//    public int updateAccountBalance(
//            @Param("accountId") Long accountId,
//            @Param("balance")BigDecimal balance
//    );
//
//    //3. 거래내역 기록
//    public int insertTransaction(AccountTransferDTO dto);

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



}
