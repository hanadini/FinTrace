package com.FinTrace.smartWallet.CustomerService.facade;

import com.FinTrace.smartWallet.CustomerService.dto.DepositDto;
import com.FinTrace.smartWallet.CustomerService.dto.FileType;
import com.FinTrace.smartWallet.CustomerService.mapper.DepositMapper;
import com.FinTrace.smartWallet.CustomerService.model.Currency;
import com.FinTrace.smartWallet.CustomerService.model.Deposit;
import com.FinTrace.smartWallet.CustomerService.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Component
public class DepositFacade {
    private final DepositMapper mapper;
    private final DepositService depositService;

    @Autowired
    public DepositFacade(DepositMapper mapper, DepositService depositService) {
        this.mapper = mapper;
        this.depositService = depositService;
    }

    public DepositDto addDeposit(Long customerId, Currency currency) {
        return mapper.toDto(depositService.addDeposit(customerId, currency));
    }

    public DepositDto depositAmount(Long id, BigDecimal amount) {
        return mapper.toDto(depositService.depositAmount(id, amount));
    }

    public DepositDto withdrawAmount(Long id, BigDecimal amount) {
        return mapper.toDto(depositService.withdrawAmount(id, amount));
    }

    public void deleteDeposit(Long id) {
        depositService.deleteDeposit(id);
    }

    public List<DepositDto> getDepositsByCustomerId(Long customerId) {
        return depositService.getDepositsByCustomerId(customerId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public DepositDto addDeposit(DepositDto deposit) {
        Deposit entity = mapper.toEntity(deposit);
        entity = depositService.addDeposit(entity);
        return mapper.toDto(entity);
    }

    public List<DepositDto> getAllDeposits() {
        return depositService.getAllDeposits()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public byte[] exportDeposits(FileType fileType) throws IOException {
        List<DepositDto> deposits = getAllDeposits();
        if(FileType.BINARY.equals(fileType)) {
            return mapper.toBytes(deposits);
        } else {
            return mapper.toJsonBytes(deposits);
        }
    }

    public void importDeposits(byte[] fileContent, FileType fileType) {
        List<DepositDto> deposits;
        if(FileType.BINARY.equals(fileType)) {
            deposits = mapper.byteToDtos(fileContent);
        } else {
            deposits = mapper.jsonToDtos(fileContent);
        }
        for (DepositDto deposit : deposits) {
            try{
                addDeposit(deposit);
            } catch (Exception e) {
                System.err.println("Failed to add deposit: " + deposit + " due to " + e.getMessage());
            }
        }
    }

    public void transferAmount(Long sourceId, Long targetId, BigDecimal amount) {
        depositService.transferAmount(sourceId, targetId, amount);
    }
}
