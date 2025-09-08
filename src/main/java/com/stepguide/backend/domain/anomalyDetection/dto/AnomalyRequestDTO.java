package com.stepguide.backend.domain.anomalyDetection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnomalyRequestDTO {
    private String accountNumber;
    private BigDecimal transactionAmount;
    private LocalDateTime transactionDateTime;
}
