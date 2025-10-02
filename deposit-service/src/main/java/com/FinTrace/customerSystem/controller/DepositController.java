package com.FinTrace.customerSystem.controller;

import com.FinTrace.customerSystem.dto.DepositDto;
import com.FinTrace.customerSystem.facade.DepositFacade;
import com.FinTrace.customerSystem.model.Currency;
import com.FinTrace.customerSystem.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/deposits")
public class DepositController {
    private final DepositFacade depositFacade;

    @Autowired
    public DepositController(DepositFacade depositFacade) {
        this.depositFacade = depositFacade;
    }

    @PostMapping("/customer/{customerId}/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPOSIT')")
    public DepositDto addDeposit(Long customerId, @RequestParam Currency currency) {
        return depositFacade.addDeposit(customerId, currency);
    }

    @PutMapping("/{id}/deposit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPOSIT')")
    public DepositDto depositAmount(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return depositFacade.depositAmount(id, amount);
    }

    @PutMapping("/{id}/withdraw")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPOSIT')")
    public DepositDto withdrawAmount(@PathVariable Long id, @RequestParam BigDecimal amount) {
        return depositFacade.withdrawAmount(id, amount);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPOSIT')")
    public List<DepositDto> getDepositsByCustomerId(@PathVariable Long customerId) {
        return depositFacade.getDepositsByCustomerId(customerId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPOSIT')")
    public void deleteDeposit(@PathVariable Long id) {
        depositFacade.deleteDeposit(id);
    }

    @PutMapping("/{fromDepositId}/transfer/{toDepositId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEPOSIT')")
    public void transferAmount(@PathVariable Long fromDepositId, @PathVariable Long toDepositId,
                               @RequestParam BigDecimal amount) {
        depositFacade.transferAmount(fromDepositId, toDepositId, amount);
    }

}
