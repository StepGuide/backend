package com.stepguide.backend.domain.fraudaccount.controller;

import com.stepguide.backend.domain.fraudaccount.dto.FraudCheckRequestDTO;
import com.stepguide.backend.domain.fraudaccount.service.FraudAccountService;
import com.stepguide.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fraud-account")
@RequiredArgsConstructor
public class FraudAccountController {
    private final FraudAccountService fraudAccountService;

    @PostMapping("/check")
    public BaseResponse<String> checkFraudAccount(@RequestBody FraudCheckRequestDTO request) {
        String response=fraudAccountService.checkFraudAccount(request.getAccountNumber());
        return new BaseResponse<>(response);

    }
}
