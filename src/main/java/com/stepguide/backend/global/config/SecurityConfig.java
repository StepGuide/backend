package com.stepguide.backend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepguide.backend.domain.user.service.JwtService;
import com.stepguide.backend.global.response.BaseResponse;
import com.stepguide.backend.global.response.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Value("${security.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        // JWT 인증 필터
        var jwtFilter = new JwtAuthenticationFilter(jwtService);

        http
                // CORS
                .cors(cors -> cors.configurationSource(req -> {
                    var c = new CorsConfiguration();
                    c.setAllowedOrigins(Arrays.asList(allowedOrigins));
                    c.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
                    c.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
                    c.setAllowCredentials(true);
                    return c;
                }))
                // CSRF 비활성화 + 무상태 세션
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 경로 권한
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/login", "/oauth/callback/kakao",
                                "/favicon.ico", "/manifest.json", "/vite.svg",
                                "/assets/**", "/static/**", "/css/**", "/js/**", "/img/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                     //   .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )

                // 인증 실패 시 JSON(BaseResponse)로 401 내려주기
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    res.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    var body = new BaseResponse<>(BaseResponseStatus.AUTH_ACCESS_TOKEN_INVALID);
                    new ObjectMapper().writeValue(res.getWriter(), body);
                }))

                // 필터 체인에 JWT 필터 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
