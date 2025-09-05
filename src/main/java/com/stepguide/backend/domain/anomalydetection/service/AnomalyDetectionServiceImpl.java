package com.stepguide.backend.domain.anomalydetection.service;

import com.stepguide.backend.domain.anomalydetection.dto.AnomalyScoreDTO;
import com.stepguide.backend.domain.anomalydetection.mapper.AnomalyDetectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AnomalyDetectionServiceImpl implements AnomalyDetectionService {
    private final AnomalyDetectionMapper anomalyDetectionMapper;

    // 이전 송금 점수(15)
    public int calculatePreviousTransferScore(Integer userId,String accountNumber, LocalDate fromDate) {
        int count=anomalyDetectionMapper.countPrevioiusTransfers(userId, accountNumber, fromDate); //이전에 송금한 횟수
        if(count>0){
            return 0;
        }
        return 15;
    }

    // 야간 송금 점수(10)
    public int calculateNightTimeTransferScore(LocalTime transactionTime){
        LocalTime nightStart=LocalTime.of(22,0); //22:00
        LocalTime nightEnd=LocalTime.of(6,0); //6:00

        // 22:00~23:59
        if (!transactionTime.isBefore(nightStart)) {
            return 10;
        }
        // 00:00~06:00
        if (!transactionTime.isAfter(nightEnd)) {
            return 10;
        }

        return 0;
    }

    // 송금 횟수 점수(10)
    public int calculateDailyTransferFrequencyScore(Integer userId, LocalDate transactionDate) {
        int count=anomalyDetectionMapper.countDailyTransfers(userId, transactionDate);
        if(count>=2){
            return 10;
        }
        return 0;


    }

    // 즐겨찾기 계좌 점수(5)
    public int calculateFavoriteAccountScore(Integer userId, String accountNumber){
        boolean exists= anomalyDetectionMapper.existsFavoriteAccount(userId, accountNumber);
        return exists?0:5;
    }

    //큰 금액 점수(20)
    public int calculateLargeAmountScore(Integer userId, BigDecimal transactionAmount, LocalDate transactionDate) {
        //1. 절대금액으로 판정
        if(transactionAmount.compareTo(BigDecimal.valueOf(500000))>=0){
            return 20;
        }
        //2. 최근 30일간 송금 평균금액의 2배이상인지로 판정
        BigDecimal avgAmount=anomalyDetectionMapper.findAvgTransferAmountLast30Days(userId, transactionDate);

        // 최근 30일 거래가 없으면 점수 부여 불가 → 0점
        if (avgAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        }
        // 현재 거래 금액이 평균송금액의 2배 이상이면 20점, 아니면 0점
        BigDecimal threshold = avgAmount.multiply(BigDecimal.valueOf(2));
        return transactionAmount.compareTo(threshold) >= 0 ? 20 : 0;
    }

    @Override
    public AnomalyScoreDTO calculateAnomalyDetectionScore(Integer userId, String accountNumber, LocalDateTime transactionDateTime, BigDecimal transactionAmount) {

        AnomalyScoreDTO dto = new AnomalyScoreDTO();

        // 이전 송금 점수(15)--그 계좌로 1년내에 송금한 적이 있는가
        LocalDate oneYearAgo = transactionDateTime.toLocalDate().minusYears(1);
        dto.setPreviousTransferScore(calculatePreviousTransferScore(userId, accountNumber, oneYearAgo));

        //큰 금액 점수(20)--1.절대금액을 넘는가?(?)만원--고려, 2. 최근 30일 평균금액*2이상인가
        dto.setLargeAmountScore(calculateLargeAmountScore(userId, transactionAmount, transactionDateTime.toLocalDate()));

        // 야간 송금 점수(10) --22:00-6:00까지인가
        dto.setNightTimeScore(calculateNightTimeTransferScore(transactionDateTime.toLocalTime()));

        // 송금 횟수 점수(10) --하루에 2회이상인가
        dto.setDailyFrequencyScore(calculateDailyTransferFrequencyScore(userId, transactionDateTime.toLocalDate()));

        // 즐겨찾기 계좌 점수(5) --즐겨찾기되어있는가
        dto.setFavoriteAccountScore(calculateFavoriteAccountScore(userId, accountNumber));

        //보호자가 화면을 보고 있는지 --고려

        // 총합 계산
        dto.calculateTotalScore();
        return dto;
    }
}
