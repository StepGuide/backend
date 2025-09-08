package com.stepguide.backend.domain.coview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStateMessage {
    private String currentLocation;     // 예: "이체 금액 입력 화면"
    private String highlightedArea;     // 예: "금액 입력 칸"
    private String userName;            // 예: "김영자님"
}
