package com.stepguide.backend.domain.anomalyDetection.controller;

import com.stepguide.backend.domain.anomalyDetection.dto.AnomalyRequestDTO;
import com.stepguide.backend.domain.anomalyDetection.dto.AnomalyScoreDTO;
import com.stepguide.backend.domain.anomalyDetection.service.AnomalyDetectionService;
import com.stepguide.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/anomaly-detection")
@RequiredArgsConstructor
public class AnomalyDetectionController {
    private final AnomalyDetectionService anomalyDetectionService;

    @PostMapping("/calculate")
    public BaseResponse<AnomalyScoreDTO> calculateAnomalyDetectionScore(@RequestBody AnomalyRequestDTO request) {
        //나중에 인증 기반 -- 수정
        Integer userId=1;

        AnomalyScoreDTO response=anomalyDetectionService.calculateAnomalyDetectionScore(
                userId,
                request.getAccountNumber(),
                request.getTransactionDateTime(),
                request.getTransactionAmount()
        );
        return new BaseResponse<>(response);

    }
}
