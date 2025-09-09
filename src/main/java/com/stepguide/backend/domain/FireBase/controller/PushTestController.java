package com.stepguide.backend.domain.FireBase.controller;

import com.stepguide.backend.domain.FireBase.service.PushSender;
import com.stepguide.backend.domain.FireBase.service.PushTokenService;
import com.stepguide.backend.domain.user.service.JwtService;
import com.stepguide.backend.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushTestController {

    private final PushSender pushSender;
    private final PushTokenService pushTokenService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/test")
    public ResponseEntity<String> test(@RequestHeader(value="Authorization", required=false) String bearer) throws Exception {
        if (bearer == null || !bearer.startsWith("Bearer ")) return ResponseEntity.status(401).body("no bearer");
        String access = bearer.substring(7);

        Long userId = Long.parseLong(jwtService.parseSubject(access)); // subject = userId

        String token = pushTokenService.findLatestActiveToken(userId)
                .orElse(null);
        if (token == null) return ResponseEntity.status(404).body("no token");

        String msgId = pushSender.sendToToken(
                token,
                "메시지 테스트!!!!!!!!!!",
                "연결 성공! 알림 클릭 시 홈으로 이동합니다.",
                "/"
        );
        return ResponseEntity.ok("sent: " + msgId);
    }

    @PostMapping("/alert-guardian")
    public ResponseEntity<String> alertGuardian(@RequestHeader(value="Authorization", required=false) String bearer) throws Exception {
        if (bearer == null || !bearer.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("no bearer");
        }
        String access = bearer.substring(7).trim();

        final Long childUserId;
        try {
            childUserId = Long.parseLong(jwtService.parseSubject(access)); // subject = userId
        } catch (Exception e) {
            return ResponseEntity.status(401).body("invalid token");
        }

        // A 사용자 이름(메시지 본문에 사용)
        String childName = userService.getUsernameById(childUserId);
        if (childName == null || childName.isBlank()) childName = "사용자";

        // 보호자 userId 찾기 (UserService에 이미 구현되어 있음)
        var guardianUserIdOpt = userService.findGuardianUserIdOf(childUserId);
        if (guardianUserIdOpt.isEmpty()) {
            return ResponseEntity.status(404).body("guardian not set");
        }
        Long guardianUserId = guardianUserIdOpt.get();

        // 보호자 최신 활성 토큰
        String guardianToken = pushTokenService.findLatestActiveToken(guardianUserId).orElse(null);
        if (guardianToken == null) {
            return ResponseEntity.status(404).body("guardian no token");
        }

        String title = "이상 탐지";
        String body  = childName + "님의 거래 활동에서 이상탐지가 있었습니다.";
        String clickUrl = "/"; // 필요하면 추후 /guardian/alerts?childId=... 로 변경

        String msgId = pushSender.sendToToken(guardianToken, title, body, clickUrl);
        return ResponseEntity.ok("sent to guardian: " + msgId);
    }
}