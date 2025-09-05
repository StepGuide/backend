package com.stepguide.backend.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/assets/**") // /assets로 시작하는 정적파일은
                .addResourceLocations("classpath:/static/assets/"); // /static/assets에서 찾도록 설정
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Vue SPA: 직접 접근도 모두 index.html로
        registry.addViewController("/{spring:[a-zA-Z0-9-_]+}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:[a-zA-Z0-9-_]+}")
                .setViewName("forward:/index.html");
    }
}
