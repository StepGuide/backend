package com.stepguide.backend.domain.anomalydetection.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnomalyDetectionServiceImplTest {
    @Autowired
    AnomalyDetectionServiceImpl anomalyDetectionService;

    @Test
    void calculatePreviousTransferScore() {
        int score= anomalyDetectionService.calculatePreviousTransferScore(1, "444-444-4444", LocalDate.of(2024, 9, 4));
        System.out.println("이전 송금 점수 조회: "+score);
    }

    @Test
    void calculateNightTimeTransferScore() {
        int score=anomalyDetectionService.calculateNightTimeTransferScore(LocalTime.of(22,0));
        System.out.println("야간 송금 점수 조회: "+score);
    }

    @Test
    void calculateLargeAmountScore() {
        int score=anomalyDetectionService.calculateLargeAmountScore(1, BigDecimal.valueOf(120000), LocalDate.of(2025,9,3));
        System.out.println("송금액 점수 조회: "+score);
    }
}