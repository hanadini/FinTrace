package com.FinTrace.customerSystem.facade;

import com.FinTrace.customerSystem.dto.DepositDto;
import com.FinTrace.customerSystem.dto.FileType;
import com.FinTrace.customerSystem.dto.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class DepositFacade {

    @Autowired
    public DepositFacade() {

    }

    public DepositDto addDeposit(Long customerId, Currency currency) {
        return new DepositDto();
    }

    public DepositDto depositAmount(Long id, BigDecimal amount) {
        return new DepositDto();
    }

    public DepositDto withdrawAmount(Long id, BigDecimal amount) {
        return new DepositDto();
    }

    public void deleteDeposit(Long id) {

    }

    public List<DepositDto> getDepositsByCustomerId(Long customerId) {
        return new ArrayList<>();
    }

    public DepositDto addDeposit(DepositDto deposit) {
        return new DepositDto();
    }

    public List<DepositDto> getAllDeposits() {
        return new ArrayList<>();
    }

    public byte[] exportDeposits(FileType fileType) throws IOException {
        return new byte[0];
    }

    public void importDeposits(byte[] fileContent, FileType fileType) {

    }

    public void transferAmount(Long sourceId, Long targetId, BigDecimal amount) {
    }

}

