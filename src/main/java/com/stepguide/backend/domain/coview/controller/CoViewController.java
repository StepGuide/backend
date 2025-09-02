package com.stepguide.backend.domain.coview.controller;


import com.stepguide.backend.domain.coview.dto.UserStateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CoViewController {


    private final SimpMessagingTemplate messagingTemplate;


    // 테스트용 ping/pong
    @MessageMapping("/ping")
    public void ping(String msg) {
        messagingTemplate.convertAndSend("/topic/pong", "pong: " + msg);
    }


    // 사용자 → 보호자 : 현재 위치 상태
    @MessageMapping("/state/{code}")
    public void sendUserState(@DestinationVariable String code, @Payload UserStateMessage message) {
        messagingTemplate.convertAndSend("/topic/state/" + code, message);
    }


    // 사용자 → 보호자 : 강조 상태 정보
    @MessageMapping("/state/highlight/{code}")
    public void handleUserState(@DestinationVariable String code, @Payload UserStateMessage message) {
        messagingTemplate.convertAndSend("/topic/highlight/" + code, message);
    }


    // 보호자 → 사용자 메시지
    @MessageMapping("/message/{code}")
    public void sendGuardianMessage(@DestinationVariable String code, @Payload String msg) {
        messagingTemplate.convertAndSend("/topic/message/" + code, msg);
    }
}