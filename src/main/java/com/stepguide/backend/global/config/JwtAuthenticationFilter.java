package com.stepguide.backend.global.config;

import com.stepguide.backend.domain.user.dto.UserDTO;
import com.stepguide.backend.domain.user.mapper.UserMapper;
import com.stepguide.backend.domain.user.service.CustomUser;
import com.stepguide.backend.domain.user.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


//  Authorization 헤더의 Bearer 액세스 토큰을 검증하여 SecurityContext에 인증을 심어주는 필터.

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserMapper userMapper; // 🔹 추가

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String accessToken = auth.substring(7);

            try {
                String subject = jwtService.parseSubject(accessToken);
                Long userId = Long.valueOf(subject);

                //DB에서 username 조회
                UserDTO user = userMapper.findById(userId);
                if (user != null) {
                    String role = "ROLE_USER"; // 현재는 USER만 사용
                    CustomUser principal = new CustomUser(userId, user.getUsername(), role);

                    var authToken = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    SecurityContextHolder.clearContext(); // 존재하지 않으면 인증 세팅 안 함
                }
            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }
}