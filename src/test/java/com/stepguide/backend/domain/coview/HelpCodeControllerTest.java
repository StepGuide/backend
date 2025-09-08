package com.stepguide.backend.domain.coview;

import com.stepguide.backend.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesRegex;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HelpCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserService userService;

    @Test
    void generateHelpCode_redis에저장되는지확인() throws Exception {
        // 1️⃣ 테스트용 유저 ID → DB에 존재하는 유저 ID로 설정
        Long testUserId = 1L;

        // 2️⃣ username 조회
        String expectedUsername = userService.getUsernameById(testUserId);

        // 3️⃣ API 호출: AuthorizationPrincipal → userId로 흘러감
        String code = mockMvc.perform(post("/api/help-code/generate")
                        .principal(() -> testUserId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesRegex("\\d{6}")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 4️⃣ Redis에 저장된 값 검증
        String savedValue = redisTemplate.opsForValue().get(code);
        assertThat(savedValue).isEqualTo(expectedUsername);

        // 5️⃣ (선택) Redis 키 TTL 확인
        Long ttl = redisTemplate.getExpire(code);
        System.out.println("⏱ TTL (seconds): " + ttl);

        // 6️⃣ Redis cleanup (선택적으로 삭제)
        redisTemplate.delete(code);
    }
}