package com.stepguide.backend.domain.accountTransfer.dto;

import com.stepguide.backend.domain.accountTransfer.entity.AccountTransferVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransferDTO {

    /*[계좌] accounts
    account_id      계좌 id (bigint)
    user_id         회원 id (bigint)
    account_number  계좌번호 (varchar(30))
    account_name    계좌이름 (varchar(100))
    balance         잔액    (decimal(15,2))
    bank_code       은행코드 (varchar(4))
    created_at      생성일자 (datetime)
    updated_at       수정일자 (datetime)
     */
    private Long accountId;         //계좌 id
    private Long userId;            //회원 id
    private String accountNumber;   //출금 계좌번호
    private String accountName;     //출금 계좌명
    private BigDecimal balance;     //출금 계좌 잔액
    private String bankCode;        //출금 계좌 은행코드
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /*[입출금 내역] transactions
    transaction_id          로그 id    (bigint)
    account_id              계좌 id    (bigint)
    status                  상태       (varchar(30))
    send_bank_code          상대은행코드 (varchar(4))
    transaction_amount      거래금액    (decimal(15,2))
    created_time             생성시각    (date)
    deposit_withdrawal      입금/출금 (deposit/withdrawal) (enum)
    payee_account_number    상대 계좌번호 (varchar(30))
    account_holder_name     예금주명    (varchar(100)
    memo                    메모       (varchar(100)
    */
    private Long transactionId;             //거래내역 로그 id
    private String status;                  //거래 상태
    private String sendBankCode;            //입금 계좌 은행코드
    private BigDecimal transactionAmount;   //거래금액
    private LocalDateTime createdTime;           //거래 시각
    private DepositWithdrawal depositWithdrawal;  //입금/출금 구분
    private String payeeAccountNumber;      //입금 계좌번호
    private String accountHolderName;       //예금주명
    private String memo;                    //메모

    public enum DepositWithdrawal{
        DEPOSIT,        //입금
        WITHDRAWAL      //출금
    }

    //즉시, 지연 구분
    private Long dealyedTransactionId;
    private TransferType transferType; //즉시 or 지연 구분
    private LocalDateTime scheduledTime; //지연 예약시간
    public enum TransferType{
        IMMEDIATE, DELAYED
    }

    // VO -> DTO
    public static AccountTransferDTO of(AccountTransferVO vo) {
        if (vo == null) return null;

        return AccountTransferDTO.builder()
                .accountId(vo.getAccountId())
                .userId(vo.getUserId())
                .accountNumber(vo.getAccountNumber())
                .accountName(vo.getAccountName())
                .balance(vo.getBalance())
                .bankCode(vo.getBankCode())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .transactionId(vo.getTransactionId())
                .status(vo.getStatus())
                .sendBankCode(vo.getSendBankCode())
                .transactionAmount(vo.getTransactionAmount())
                .createdTime(vo.getCreatedTime())
                .depositWithdrawal(vo.getDepositWithdrawal())
                .payeeAccountNumber(vo.getPayeeAccountNumber())
                .accountHolderName(vo.getAccountHolderName())
                .memo(vo.getMemo())
                .build();
    }

    // DTO -> VO
    public AccountTransferVO toVo() {
        return AccountTransferVO.builder()
                .accountId(this.getAccountId())
                .userId(this.getUserId())
                .accountNumber(this.getAccountNumber())
                .accountName(this.getAccountName())
                .balance(this.getBalance())
                .bankCode(this.getBankCode())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .transactionId(this.getTransactionId())
                .status(this.getStatus())
                .sendBankCode(this.getSendBankCode())
                .transactionAmount(this.getTransactionAmount())
                .createdTime(this.getCreatedTime())
                .depositWithdrawal(this.getDepositWithdrawal())
                .payeeAccountNumber(this.getPayeeAccountNumber())
                .accountHolderName(this.getAccountHolderName())
                .memo(this.getMemo())
                .build();
    }


}
