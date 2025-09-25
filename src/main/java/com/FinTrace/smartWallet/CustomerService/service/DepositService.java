package com.FinTrace.smartWallet.CustomerService.service;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.dao.DepositDao;
import com.FinTrace.smartWallet.CustomerService.exception.CustomerNotFoundException;
import com.FinTrace.smartWallet.CustomerService.exception.DepositNotFoundException;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.Deposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositService {
    private final DepositDao depositDao;
    private final CustomerDao customerDao;

    @Autowired
    public DepositService(DepositDao depositDao, CustomerDao customerDao) {
        this.depositDao = depositDao;
        this.customerDao = customerDao;
    }

    public Deposit addDeposit(long customerId) {
        Customer customer = customerDao.findById(customerId).orElseThrow(() ->
                new CustomerNotFoundException("Customer with id " + customerId + " does not exist."));
        Deposit deposit = Deposit.builder()
                .amount(0.0)
                .customer(customer)
                .build();
        return depositDao.save(deposit);
    }

    public Deposit addDeposit(Deposit deposit) {
        return depositDao.save(deposit);
    }

    public Deposit depositAmount(Long id, Double amount) {
        Deposit deposit = depositDao.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit with id " + id + " does not exist."));
        deposit.setAmount(deposit.getAmount() + amount);
        return depositDao.save(deposit);
    }

    public Deposit withdrawAmount(Long id, Double amount) {
        Deposit deposit = depositDao.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit with id " + id + " does not exist."));
        if (deposit.getAmount() < amount) {
            throw new IllegalArgumentException("Insufficient funds in deposit with id " + id);
        }
        deposit.setAmount(deposit.getAmount() - amount);
        return depositDao.save(deposit);
    }

    public void deleteDeposit(Long id) {
        if (depositDao.findById(id).isPresent()) {
            depositDao.deleteById(id);
        } else {
            throw new DepositNotFoundException("Deposit with id " + id + " does not exist.");
        }
    }

    public List<Deposit> getDepositsByCustomerId(Long customerId) {
        return depositDao.findByCustomerId(customerId);
    }

    public List<Deposit> getAllDeposits() {
        return depositDao.findAll();
    }

}
