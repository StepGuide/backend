package com.stepguide.backend.domain.anomalydetection.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyScoreDTO {
    private int previousTransferScore;   // 이전 송금 점수
    private int largeAmountScore;        // 큰 금액 점수
    private int nightTimeScore;          // 야간 송금 점수
    private int dailyFrequencyScore;     // 하루 송금 횟수 점수
    private int favoriteAccountScore;    // 즐겨찾기 계좌 점수
    private int totalScore;              // 총합 점수

    public void calculateTotalScore() {
        this.totalScore=previousTransferScore + largeAmountScore + nightTimeScore
                + dailyFrequencyScore + favoriteAccountScore;
    }
}
