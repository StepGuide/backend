package com.stepguide.backend.domain.user.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class HashUtil {
    private HashUtil() {}

//  문자열을 SHA-256으로 해시하여 32바이트 배열로 반환.
    public static byte[] sha256ToBytes(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value는 null이 안됩니다.");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // 서비스단에서 BaseException으로 감싸 처리하므로 여기서는 런타임 예외로 던져도 충분
            throw new RuntimeException("SHA-256 hashing failed", e);
        }
    }
}
