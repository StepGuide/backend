package com.stepguide.backend.domain.coview.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepguide.backend.domain.coview.dto.UserStateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CoViewController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    // WebRTC 시그널링 메시지 처리 (개선된 버전)
    @MessageMapping("/webrtc/{code}")
    public void handleWebRTCSignaling(@DestinationVariable String code, @Payload String message) {
        try {
            // 메시지 크기 로깅
            log.info("WebRTC 시그널링 메시지 수신 - 코드: {}, 크기: {} bytes", code, message.length());

            // JSON 파싱 검증
            JsonNode messageNode = objectMapper.readTree(message);
            String type = messageNode.get("type").asText();
            JsonNode data = messageNode.get("data");

            log.info("WebRTC 메시지 파싱 성공 - 타입: {}, 데이터 존재: {}", type, data != null);

            // 메시지 크기가 너무 큰 경우 경고
            if (message.length() > 32768) {
                log.warn("WebRTC 메시지 크기가 큽니다: {} bytes (코드: {})", message.length(), code);
            }

            // WebRTC 시그널링 메시지를 그대로 브로드캐스트
            messagingTemplate.convertAndSend("/topic/webrtc/" + code, message);

            log.info("WebRTC 메시지 브로드캐스트 완료 - 코드: {}", code);

        } catch (JsonProcessingException e) {
            log.error("WebRTC 메시지 JSON 파싱 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            // 에러 메시지를 클라이언트에 전송
            messagingTemplate.convertAndSend("/topic/webrtc/" + code,
                    "{\"type\":\"error\",\"message\":\"JSON 파싱 실패: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("WebRTC 메시지 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            // 에러 메시지를 클라이언트에 전송
            messagingTemplate.convertAndSend("/topic/webrtc/" + code,
                    "{\"type\":\"error\",\"message\":\"메시지 처리 실패: " + e.getMessage() + "\"}");
        }
    }

    // WebRTC 연결 상태 확인
    @MessageMapping("/webrtc/status/{code}")
    public void handleWebRTCStatus(@DestinationVariable String code, @Payload String status) {
        try {
            log.info("WebRTC 연결 상태 업데이트 - 코드: {}, 상태: {}", code, status);
            messagingTemplate.convertAndSend("/topic/webrtc/status/" + code, status);
        } catch (Exception e) {
            log.error("WebRTC 상태 메시지 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        }
    }

    // WebRTC 에러 처리
    @MessageMapping("/webrtc/error/{code}")
    public void handleWebRTCError(@DestinationVariable String code, @Payload String error) {
        try {
            log.error("WebRTC 에러 수신 - 코드: {}, 에러: {}", code, error);
            messagingTemplate.convertAndSend("/topic/webrtc/error/" + code, error);
        } catch (Exception e) {
            log.error("WebRTC 에러 메시지 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        }
    }
}