package com.stepguide.backend.domain.anomalydetection.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnomalyDetectionMapperTest {
    @Autowired
    AnomalyDetectionMapper anomalyDetectionMapper;
    @Test
    void countPrevioiusTransfers() {
        int count=anomalyDetectionMapper.countPrevioiusTransfers(1,"333-3333-3333", LocalDate.of(2025, 9,3));
        System.out.println("송금 횟수: "+count);
    }

    @Test
    void countDailyTransfers() {
        int count=anomalyDetectionMapper.countDailyTransfers(1, LocalDate.of(2025, 9, 1));
        System.out.println("송금 횟수: "+count);
    }

    @Test
    void existsFavoriteAccount() {
        boolean exists=anomalyDetectionMapper.existsFavoriteAccount(1, "333-3333-3333");
        System.out.println("즐겨찾기 계좌 여부: "+exists);
    }
}