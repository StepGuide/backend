package com.stepguide.backend.domain.accountTransfer.external;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/postonetest")
public class PortOneTestContoller {

    private final PortOneClient portOneClient;

    @GetMapping("/account-holder")
    public String getAccountHolder(
            @RequestParam String bankCode,
            @RequestParam String accountNumber
    ){
        return portOneClient.getAccountHolderName(bankCode, accountNumber);
    }

    // [TEST]
    //GET http://localhost:8080/api/postonetest/account-holder?bankCode=&accountNumber=
}
