package com.stepguide.backend.domain.accountTransfer.scheduler;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;
import com.stepguide.backend.domain.accountTransfer.mapper.AccountTransferMapper;
import com.stepguide.backend.domain.accountTransfer.service.AccountTransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class DelayedTransferScheduler {
    private final AccountTransferMapper accountTransferMapper;
    private final AccountTransferService accountTransferService;

    @Scheduled(fixedDelay=60000) //1분마다 확인
    public void processPendingTransfers(){
        LocalDateTime now = LocalDateTime.now();
        //validate: 계좌번호, 은행코드-->계좌번호, 은행코드, 상대 예금주, accountId
        //processImmediateTransfer: 상대은행코드, 보내는 금액, 상대계좌번호-->상태, 출금/인출, 생성시간
        List<AccountTransferDTO> pending=accountTransferMapper.findDelayedTransactionsReady(now);

        for(AccountTransferDTO dto:pending){
            try{
                accountTransferService.processDelayedTransfer(dto);
            }
            catch(Exception e){
                //실패 상태로 업데이트
                accountTransferMapper.updateDelayedTransactionStatus(dto.getDealyedTransactionId(), "FAIL");
                log.error("지연 이체 처리 실패: {}", dto.getDealyedTransactionId(), e);
            }
        }
    }
}
