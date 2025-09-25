package com.FinTrace.smartWallet.CustomerService.controller;

import com.FinTrace.smartWallet.CustomerService.dto.DepositDto;
import com.FinTrace.smartWallet.CustomerService.facade.DepositFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public DepositDto addDeposit(Long customerId) {
        return depositFacade.addDeposit(customerId);
    }

    @PutMapping("/{id}/deposit")
    public DepositDto depositAmount(@PathVariable Long id, @RequestParam double amount) {
        return depositFacade.depositAmount(id, amount);
    }

    @PutMapping("/{id}/withdraw")
    public DepositDto withdrawAmount(@PathVariable Long id, @RequestParam double amount) {
        return depositFacade.withdrawAmount(id, amount);
    }

    @GetMapping("/{customerId}")
    public List<DepositDto> getDepositsByCustomerId(@PathVariable Long customerId) {
        return depositFacade.getDepositsByCustomerId(customerId);
    }

    @DeleteMapping("/{id}")
    public void deleteDeposit(@PathVariable Long id) {
        depositFacade.deleteDeposit(id);
    }
}
