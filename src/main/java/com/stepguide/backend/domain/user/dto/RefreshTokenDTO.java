package com.stepguide.backend.domain.user.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenDTO {
    private Long id;
    private Long userId;
    private byte[] tokenHash;        // BINARY(32)
    private LocalDateTime expiresAt; // DATETIME
    private boolean revoked;         // TINYINT(1)
    private Long rotatedFrom;        // BIGINT (nullable)
    private LocalDateTime createdAt; // DATETIME
}