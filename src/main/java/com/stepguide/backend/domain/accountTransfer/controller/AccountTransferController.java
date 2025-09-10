package com.stepguide.backend.domain.accountTransfer.controller;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.service.AccountTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class AccountTransferController {

    private final AccountTransferService accountTransferService;

    // 1단계 : 내 계좌 조회
    @GetMapping("/accounts/{userId}")
    public ResponseEntity<List<AccountTransferDTO>> getUserAccounts(@PathVariable Long userId) {
        return ResponseEntity.ok(accountTransferService.getUserAccounts(userId));
    }

    //1.1단계 : 최근 계좌 조회
    @GetMapping("/transactions/{accountId}")
    public ResponseEntity<List<AccountTransferDTO>> getAccountTransfer(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountTransferService.getAccountTransactions(accountId));
    }

    // 2단계: 검증 (사용자 입력 → 서버 확인 후 반환)
    @PostMapping("/validate")
    public ResponseEntity<AccountTransferDTO> validateTransfer(@RequestBody AccountTransferDTO dto) {
        AccountTransferDTO validated = accountTransferService.validateTransfer(dto);
        return ResponseEntity.ok(validated);
    }

    // 3단계: 최종 실행
    @PostMapping("/execute")
    public ResponseEntity<String> executeTransfer(@RequestBody AccountTransferDTO dto) {
        accountTransferService.executeTransfer(dto);

        String transferTypeKor = switch (dto.getTransferType()) {
            case IMMEDIATE -> "즉시 이체";
            case DELAYED -> "지연 이체";
        };

        return ResponseEntity.ok(transferTypeKor+" 요청이 처리되었습니다.");


    }

}
