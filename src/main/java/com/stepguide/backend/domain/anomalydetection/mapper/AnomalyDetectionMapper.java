package com.stepguide.backend.domain.anomalydetection.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AnomalyDetectionMapper {
    int countPrevioiusTransfers(@Param("userId") Integer userId, @Param("accountNumber") String accountNumber, @Param("fromDate") LocalDate fromDate);

    int countDailyTransfers(@Param("userId") Integer userId, @Param("transactionDate") LocalDate transactionDate);

    boolean existsFavoriteAccount(@Param("userId") Integer userId, @Param("accountNumber") String accountNumber);

    BigDecimal findAvgTransferAmountLast30Days(@Param("userId") Integer userId, @Param("transactioDate") LocalDate transactionDate);
}
