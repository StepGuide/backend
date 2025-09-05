package com.stepguide.backend.domain.anomalydetection.controller;

import com.stepguide.backend.domain.anomalydetection.dto.AnomalyScoreDTO;
import com.stepguide.backend.domain.anomalydetection.service.AnomalyDetectionService;
import com.stepguide.backend.domain.fraudaccount.service.FraudAccountService;
import com.stepguide.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/anomaly-detection")
@RequiredArgsConstructor
public class AnomalyDetectionController {
    private final AnomalyDetectionService anomalyDetectionService;

    @PostMapping("/calculate")
    public BaseResponse<AnomalyScoreDTO> calculateAnomalyDetectionScore(@RequestParam String accountNumber, @RequestParam BigDecimal transactionAmount) {
        //userId 수정
        Integer userId=1;

        //현재 시각 가져오기
        LocalDateTime transactionDateTime= LocalDateTime.now();


        AnomalyScoreDTO response=anomalyDetectionService.calculateAnomalyDetectionScore(userId,accountNumber, transactionDateTime, transactionAmount);
        return new BaseResponse<>(response);

    }
}
