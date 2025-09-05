package com.stepguide.backend.domain.anomalydetection.service;

import com.stepguide.backend.domain.anomalydetection.dto.AnomalyScoreDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface AnomalyDetectionService {
    AnomalyScoreDTO calculateAnomalyDetectionScore(Integer userId, String accountNumber, LocalDateTime transactionTime, BigDecimal transactionAmount);
}
