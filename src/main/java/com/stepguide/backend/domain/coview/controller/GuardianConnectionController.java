package com.stepguide.backend.domain.coview.controller;

import com.stepguide.backend.domain.coview.dto.HelpCodeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guardian")
public class GuardianConnectionController {

    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/connect")
    public ResponseEntity<?> connectToUser(@RequestBody HelpCodeRequest request){
        String code= request.getCode();
        String redisKey="help-code:"+code;

        String userId=redisTemplate.opsForValue().get(redisKey);
        if(userId==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("도움 코드를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(
                Map.of("userId", userId, "code", code, "status", "connected")
        );
    }

}
