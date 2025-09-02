package com.stepguide.backend.global.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpaNoResourceAdvice {

    @ExceptionHandler(NoResourceFoundException.class)
    public String forwardSpa(NoResourceFoundException ex, HttpServletRequest req) {
        Object uriObj = req.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String uri = (uriObj != null) ? uriObj.toString() : req.getRequestURI();

        // API나 실제 파일 요청은 기존 흐름 유지
        if (uri.startsWith("/api") || uri.contains(".")) {
            return "forward:/error";
        }
        // 그 외 비-API 경로는 SPA 진입점으로
        return "forward:/index.html";
    }
}