package com.stepguide.backend.domain.fraudaccount.controller;

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
    public BaseResponse<String> checkFraudAccount(@RequestParam String accountNumber) {
        String response=fraudAccountService.checkFraudAccount(accountNumber);
        return new BaseResponse<>(response);

    }
}
