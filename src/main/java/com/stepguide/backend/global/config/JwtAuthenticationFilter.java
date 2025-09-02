package com.stepguide.backend.global.config;

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

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {

        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String accessToken = auth.substring(7);
            try {
                String subject = jwtService.parseSubject(accessToken);
                var authToken = new UsernamePasswordAuthenticationToken(
                        subject, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (JwtException e) {
                // 토큰이 만료/위조 등일 때 인증은 세팅하지 않고 흘려보냄(EntryPoint가 401 응답)
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }
}