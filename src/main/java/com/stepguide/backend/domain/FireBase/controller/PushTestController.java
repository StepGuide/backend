package com.stepguide.backend.domain.FireBase.controller;

import com.stepguide.backend.domain.FireBase.service.PushSender;
import com.stepguide.backend.domain.FireBase.service.PushTokenService;
import com.stepguide.backend.domain.user.service.JwtService;
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
}