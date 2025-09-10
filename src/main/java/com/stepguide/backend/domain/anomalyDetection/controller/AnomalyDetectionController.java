package com.stepguide.backend.domain.anomalyDetection.controller;

import com.stepguide.backend.domain.anomalyDetection.dto.AnomalyRequestDTO;
import com.stepguide.backend.domain.anomalyDetection.dto.AnomalyScoreDTO;
import com.stepguide.backend.domain.anomalyDetection.service.AnomalyDetectionService;
import com.stepguide.backend.domain.user.service.JwtService;
import com.stepguide.backend.global.response.BaseResponse;
import com.stepguide.backend.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/anomaly-detection")
@RequiredArgsConstructor
@Log
public class AnomalyDetectionController {
    private final AnomalyDetectionService anomalyDetectionService;
    private final JwtService jwtService;

    @PostMapping("/calculate")
    public BaseResponse<AnomalyScoreDTO> calculateAnomalyDetectionScore(@RequestBody AnomalyRequestDTO request, @RequestHeader(value="Authorization", required=false) String bearer) {
        //나중에 인증 기반 -- 수정
//        Integer userId=1;
        if (bearer == null || !bearer.startsWith("Bearer ")){
            log.info("access token이 제대로 작성되지 않음");
            return new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED);}


        Integer userId;
        String access=bearer.substring(7).trim();
        userId=Integer.parseInt(jwtService.parseSubject(access));
        log.info(""+userId);

        LocalDateTime now = LocalDateTime.now();

        AnomalyScoreDTO response=anomalyDetectionService.calculateAnomalyDetectionScore(
                userId,
                request.getAccountNumber(),
                now,
                request.getTransactionAmount()
        );
        return new BaseResponse<>(response);

    }
}
