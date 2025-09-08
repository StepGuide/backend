package com.stepguide.backend.domain.anomalyDetection.service;

import com.stepguide.backend.domain.anomalyDetection.dto.AnomalyScoreDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AnomalyDetectionService {
    AnomalyScoreDTO calculateAnomalyDetectionScore(Integer userId, String accountNumber, LocalDateTime transactionTime, BigDecimal transactionAmount);
}
