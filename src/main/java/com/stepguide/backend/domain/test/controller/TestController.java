package com.stepguide.backend.domain.test.controller;

import com.stepguide.backend.global.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public ApiResponse<String> hello() {
        return ApiResponse.success("Vue와 통신 성공!");
    }
}
