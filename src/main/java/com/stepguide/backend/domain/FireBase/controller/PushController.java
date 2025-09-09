package com.stepguide.backend.domain.FireBase.controller;

import com.stepguide.backend.domain.FireBase.dto.RegisterTokenDTO;
import com.stepguide.backend.domain.FireBase.service.PushTokenService;
import com.stepguide.backend.domain.user.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushController {
    private final PushTokenService pushTokenService;
    private final JwtService jwtService;

    @PostMapping("/register-token")
    public ResponseEntity<Void> register(@RequestBody RegisterTokenDTO req,
                                         @RequestHeader(value="Authorization", required=false) String bearer,
                                         @RequestHeader(value="User-Agent", required=false) String ua) {
        if (bearer == null || !bearer.startsWith("Bearer ")) return ResponseEntity.status(401).build();
        if (req.getToken() == null || req.getToken().isBlank()) return ResponseEntity.badRequest().build();

        final Long userId;
        try {
            String access = bearer.substring(7).trim();
            userId = Long.parseLong(jwtService.parseSubject(access));
        } catch (Exception e) {
            return ResponseEntity.status(401).build(); // JWT가 비정상이면 401로
        }

        pushTokenService.register(userId, req.getToken(), ua);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unregister-token")
    public ResponseEntity<Void> unregister(@RequestBody RegisterTokenDTO req,
                                           @RequestHeader(value="Authorization", required=false) String bearer) {
        if (req == null || req.getToken() == null || req.getToken().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        pushTokenService.unsubscribeByToken(req.getToken());
        return ResponseEntity.ok().build();
    }
}