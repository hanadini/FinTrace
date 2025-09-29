package com.FinTrace.smartWallet.CustomerService.service;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.dao.DepositDao;
import com.FinTrace.smartWallet.CustomerService.exception.CustomerNotFoundException;
import com.FinTrace.smartWallet.CustomerService.exception.DepositNotFoundException;
import com.FinTrace.smartWallet.CustomerService.exception.InsufficientFundException;
import com.FinTrace.smartWallet.CustomerService.model.Currency;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.Deposit;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DepositService {
    private final DepositDao depositDao;
    private final CustomerDao customerDao;

    private final CurrencyService currencyService;

    @Autowired
    public DepositService(DepositDao depositDao, CustomerDao customerDao, CurrencyService currencyService) {
        this.depositDao = depositDao;
        this.customerDao = customerDao;
        this.currencyService = currencyService;
    }

    public Deposit addDeposit(Long customerId, Currency currency) {
        Deposit deposit = new Deposit();
        deposit.setCurrency(currency);
        deposit.setAmount(BigDecimal.ZERO); // Initialize with zero amount
        deposit.setCustomer(customerDao.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " does not exist.")));
        return depositDao.save(deposit);
    }

    public Deposit addDeposit(Deposit deposit) {
        return depositDao.save(deposit);
    }

    public Deposit depositAmount(Long id, BigDecimal amount) {
        for(int i = 0; i < 10; i++) {
            try {
                return performDeposit(id, amount);
            } catch (ObjectOptimisticLockingFailureException e) {
                try {
                    Thread.sleep(100); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    throw new RuntimeException("Thread was interrupted while waiting to retry deposit operation.", ie);
                }
            }
        }
        throw new RuntimeException("Failed to deposit amount after multiple retries due to concurrent modifications.");
    }

    @Transactional
    private Deposit performDeposit(Long id, BigDecimal amount) {
        Deposit deposit = depositDao.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit with id " + id + " does not exist."));
        deposit.setAmount(deposit.getAmount().add(amount));
        Deposit savedDeposit = depositDao.save(deposit);
        depositDao.flush(); // Ensure the changes are written to the database
        return savedDeposit;
    }

    @Transactional(rollbackFor = InsufficientFundException.class)
    public Deposit withdrawAmount(Long id, BigDecimal amount) {
        Deposit deposit = depositDao.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit with id " + id + " does not exist."));
        deposit.setAmount(deposit.getAmount().subtract(amount));
        if (deposit.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundException("Insufficient funds in deposit with id " + id);
        }
        return depositDao.save(deposit);
    }

    @Transactional
    public void transferAmount(Long sourceId, Long targetId, BigDecimal amount) {
        Deposit sourceDeposit = depositDao.findById(sourceId)
                .orElseThrow(() -> new DepositNotFoundException("Source deposit with id " + sourceId + " does not exist."));
        Deposit targetDeposit = depositDao.findById(targetId)
                .orElseThrow(() -> new DepositNotFoundException("Target deposit with id " + targetId + " does not exist."));

        if (sourceDeposit.getAmount().compareTo(amount) < 0) {
            throw new InsufficientFundException("Insufficient funds in source deposit with id " + sourceId);
        }

        // convert amount to the source currency if needed and do calculation with scale 2
        if (!sourceDeposit.getCurrency().equals(targetDeposit.getCurrency())) {
            double exchangeRate = currencyService
                    .getExchangeRate(sourceDeposit.getCurrency().name(), targetDeposit.getCurrency().name());
            if (exchangeRate <= 0) {
                throw new IllegalArgumentException("Invalid exchange rate for currency conversion.");
            }
            amount = amount.multiply(BigDecimal.valueOf(exchangeRate)).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        
        sourceDeposit.setAmount(sourceDeposit.getAmount().subtract(amount));
        targetDeposit.setAmount(targetDeposit.getAmount().add(amount));

        depositDao.save(sourceDeposit);
        depositDao.save(targetDeposit);
        depositDao.flush(); // Force flush to ensure all changes are persisted
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
