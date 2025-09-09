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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CoViewController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

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

    /**
     * WebRTC Offer 처리 (사용자 → 보호자)
     */
    @MessageMapping("/webrtc/offer/{code}")
    public void handleWebRTCOffer(@DestinationVariable String code, @Payload String offerJson) {
        try {
            log.info("WebRTC Offer 수신 - 코드: {}, 크기: {} bytes", code, offerJson.length());

            // JSON 파싱 검증
            JsonNode offerNode = objectMapper.readTree(offerJson);
            String type = offerNode.get("type").asText();

            if (!"offer".equals(type)) {
                log.warn("잘못된 Offer 타입 - 코드: {}, 타입: {}", code, type);
                return;
            }

            // Offer에 메타데이터 추가
            String enhancedOffer = objectMapper.writeValueAsString(Map.of(
                    "code", code,
                    "type", "offer",
                    "sdp", offerNode.get("sdp").asText(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "serverReceived", true
            ));

            // 보호자에게 Offer 전송
            messagingTemplate.convertAndSend("/topic/webrtc/offer/" + code, enhancedOffer);
            log.info("WebRTC Offer 브로드캐스트 완료 - 코드: {}", code);

        } catch (JsonProcessingException e) {
            log.error("WebRTC Offer JSON 파싱 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            sendWebRTCError(code, "Offer JSON 파싱 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("WebRTC Offer 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            sendWebRTCError(code, "Offer 처리 실패: " + e.getMessage());
        }
    }

    /**
     * WebRTC Answer 처리 (보호자 → 사용자)
     */
    @MessageMapping("/webrtc/answer/{code}")
    public void handleWebRTCAnswer(@DestinationVariable String code, @Payload String answerJson) {
        try {
            log.info("WebRTC Answer 수신 - 코드: {}, 크기: {} bytes", code, answerJson.length());

            // JSON 파싱 검증
            JsonNode answerNode = objectMapper.readTree(answerJson);
            String type = answerNode.get("type").asText();

            if (!"answer".equals(type)) {
                log.warn("잘못된 Answer 타입 - 코드: {}, 타입: {}", code, type);
                return;
            }

            // Answer에 메타데이터 추가
            String enhancedAnswer = objectMapper.writeValueAsString(Map.of(
                    "code", code,
                    "type", "answer",
                    "sdp", answerNode.get("sdp").asText(),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "serverReceived", true
            ));

            // 사용자에게 Answer 전송
            messagingTemplate.convertAndSend("/topic/webrtc/answer/" + code, enhancedAnswer);
            log.info("WebRTC Answer 브로드캐스트 완료 - 코드: {}", code);

        } catch (JsonProcessingException e) {
            log.error("WebRTC Answer JSON 파싱 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            sendWebRTCError(code, "Answer JSON 파싱 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("WebRTC Answer 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            sendWebRTCError(code, "Answer 처리 실패: " + e.getMessage());
        }
    }

    /**
     * WebRTC ICE 후보 처리
     */
    @MessageMapping("/webrtc/ice/{code}")
    public void handleWebRTCIceCandidate(@DestinationVariable String code, @Payload String iceJson) {
        try {
            log.info("WebRTC ICE 후보 수신 - 코드: {}, 크기: {} bytes", code, iceJson.length());

            // JSON 파싱 검증
            JsonNode iceNode = objectMapper.readTree(iceJson);
            String type = iceNode.get("type").asText();

            if (!"ice-candidate".equals(type)) {
                log.warn("잘못된 ICE 후보 타입 - 코드: {}, 타입: {}", code, type);
                return;
            }

            // ICE 후보에 메타데이터 추가
            String enhancedIce = objectMapper.writeValueAsString(Map.of(
                    "code", code,
                    "type", "ice-candidate",
                    "candidate", iceNode.get("candidate"),
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "serverReceived", true
            ));

            // 상대방에게 ICE 후보 전송
            messagingTemplate.convertAndSend("/topic/webrtc/ice/" + code, enhancedIce);
            log.info("WebRTC ICE 후보 브로드캐스트 완료 - 코드: {}", code);

        } catch (JsonProcessingException e) {
            log.error("WebRTC ICE 후보 JSON 파싱 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            sendWebRTCError(code, "ICE 후보 JSON 파싱 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("WebRTC ICE 후보 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
            sendWebRTCError(code, "ICE 후보 처리 실패: " + e.getMessage());
        }
    }

    /**
     * WebRTC 연결 종료 처리
     */
    @MessageMapping("/webrtc/end/{code}")
    public void handleWebRTCEnd(@DestinationVariable String code, @Payload String endJson) {
        try {
            log.info("WebRTC 연결 종료 수신 - 코드: {}, 메시지: {}", code, endJson);

            // 연결 종료 메시지에 타임스탬프 추가
            String enhancedEnd = objectMapper.writeValueAsString(Map.of(
                    "code", code,
                    "type", "end",
                    "message", endJson,
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "serverReceived", true
            ));

            // 상대방에게 연결 종료 알림
            messagingTemplate.convertAndSend("/topic/webrtc/end/" + code, enhancedEnd);
            log.info("WebRTC 연결 종료 브로드캐스트 완료 - 코드: {}", code);

        } catch (JsonProcessingException e) {
            log.error("WebRTC 연결 종료 JSON 파싱 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        } catch (Exception e) {
            log.error("WebRTC 연결 종료 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        }
    }

    /**
     * WebRTC 연결 상태 확인
     */
    @MessageMapping("/webrtc/status/{code}")
    public void handleWebRTCStatus(@DestinationVariable String code, @Payload String status) {
        try {
            log.info("WebRTC 연결 상태 업데이트 - 코드: {}, 상태: {}", code, status);

            // 상태 메시지에 타임스탬프 추가
            String enhancedStatus = objectMapper.writeValueAsString(Map.of(
                    "code", code,
                    "status", status,
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "serverReceived", true
            ));

            messagingTemplate.convertAndSend("/topic/webrtc/status/" + code, enhancedStatus);
            log.info("WebRTC 상태 메시지 브로드캐스트 완료 - 코드: {}", code);

        } catch (JsonProcessingException e) {
            log.error("WebRTC 상태 메시지 JSON 파싱 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        } catch (Exception e) {
            log.error("WebRTC 상태 메시지 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        }
    }

    /**
     * WebRTC 에러 처리
     */
    @MessageMapping("/webrtc/error/{code}")
    public void handleWebRTCError(@DestinationVariable String code, @Payload String error) {
        try {
            log.error("WebRTC 에러 수신 - 코드: {}, 에러: {}", code, error);

            // 에러 메시지에 타임스탬프 추가
            String enhancedError = objectMapper.writeValueAsString(Map.of(
                    "code", code,
                    "error", error,
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "serverReceived", true
            ));

            messagingTemplate.convertAndSend("/topic/webrtc/error/" + code, enhancedError);
            log.info("WebRTC 에러 메시지 브로드캐스트 완료 - 코드: {}", code);

        } catch (JsonProcessingException e) {
            log.error("WebRTC 에러 메시지 JSON 파싱 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        } catch (Exception e) {
            log.error("WebRTC 에러 메시지 처리 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        }
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * WebRTC 에러 전송 헬퍼 메서드
     */
    private void sendWebRTCError(String code, String errorMessage) {
        try {
            String errorJson = objectMapper.writeValueAsString(Map.of(
                    "type", "error",
                    "message", errorMessage,
                    "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "serverGenerated", true
            ));

            messagingTemplate.convertAndSend("/topic/webrtc/error/" + code, errorJson);
        } catch (Exception e) {
            log.error("WebRTC 에러 전송 실패 - 코드: {}, 에러: {}", code, e.getMessage());
        }
    }


}