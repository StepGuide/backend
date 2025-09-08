package com.stepguide.backend.domain.coview.controller;

import com.stepguide.backend.domain.coview.service.HelpCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/help-code")
@RequiredArgsConstructor
public class HelpCodeController {
    private final HelpCodeService helpCodeService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateHelpCode(@RequestParam String userId) {
        // 6자리 랜덤 코드 생성
        String code = generateRandomCode();

        // Redis에 저장
        helpCodeService.saveHelpCode(code, userId);

        return ResponseEntity.ok(code);
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10)); // 0~9
        }
        return sb.toString();
    }
}
