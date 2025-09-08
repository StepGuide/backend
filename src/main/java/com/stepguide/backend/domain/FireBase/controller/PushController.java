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
        String access = bearer.substring(7);

        Long userId = Long.parseLong(jwtService.parseSubject(access)); // ⬅️ subject = userId

        pushTokenService.register(userId, req.getToken(), ua);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unregister-token")
    public ResponseEntity<Void> unregister(@RequestBody RegisterTokenDTO req,
                                           @RequestHeader(value="Authorization", required=false) String bearer) {
        if (bearer == null || !bearer.startsWith("Bearer "))
            return ResponseEntity.status(401).build();
        if (req.getToken() == null || req.getToken().isBlank())
            return ResponseEntity.badRequest().build();

        String access = bearer.substring(7);
        Long userId = Long.parseLong(jwtService.parseSubject(access)); // subject = userId
        pushTokenService.unsubscribe(userId, req.getToken());          // subscribed=0
        return ResponseEntity.ok().build();
    }
}