package com.stepguide.backend.domain.accountTransfer.service;

import com.stepguide.backend.domain.accountTransfer.dto.AccountTransferDTO;

public interface AccountTransferService {

    void transfer(AccountTransferDTO dto);

}
