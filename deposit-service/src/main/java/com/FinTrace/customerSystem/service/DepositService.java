package com.FinTrace.customerSystem.service;

import com.FinTrace.customerSystem.dao.DepositDao;
import com.FinTrace.customerSystem.exception.CustomerNotFoundException;
import com.FinTrace.customerSystem.exception.DepositNotFoundException;
import com.FinTrace.customerSystem.exception.InsufficientFundsException;
import com.FinTrace.customerSystem.model.Currency;
import com.FinTrace.customerSystem.model.Deposit;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DepositService {
    private final DepositDao depositDao;

    private final CurrencyService currencyService;

    @Autowired
    public DepositService(DepositDao depositDao,
                          CurrencyService customerService) {
        this.depositDao = depositDao;
        this.currencyService = customerService;
    }

    public Deposit addDeposit(Long customerId, Currency currency) {
        Deposit deposit = new Deposit();
        deposit.setCustomerId(customerId);
        deposit.setCurrency(currency);
        deposit.setAmount(BigDecimal.ZERO); // Initialize with zero amount
        return depositDao.save(deposit);
    }

    public Deposit addDeposit(Deposit deposit) {
        return depositDao.save(deposit);
    }

    public Deposit depositAmount(Long id, BigDecimal amount) {
        for (int i = 0; i < 10; i++) {
            try {
                return performDeposit(id, amount);
            } catch (ObjectOptimisticLockingFailureException e) {
                try {
                    Thread.sleep(100); // Wait before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread was interrupted while waiting to retry deposit operation.", ie);
                }
            }
        }
        throw new RuntimeException("Failed to deposit amount after multiple retries due to concurrent modifications.");
    }

    @Transactional
    protected Deposit performDeposit(Long id, BigDecimal amount) {
        Deposit deposit = depositDao.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit with id " + id + " does not exist."));
        deposit.setAmount(deposit.getAmount().add(amount));
        Deposit savedDeposit = depositDao.save(deposit);
        depositDao.flush(); // Force flush to detect optimistic locking issues
        return savedDeposit;
    }

    @Transactional(rollbackFor = InsufficientFundsException.class)
    public Deposit withdrawAmount(Long id, BigDecimal amount) {
        Deposit deposit = depositDao.findById(id)
                .orElseThrow(() -> new DepositNotFoundException("Deposit with id " + id + " does not exist."));
        deposit.setAmount(deposit.getAmount().subtract(amount));
        if (deposit.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds in deposit with id " + id);
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
            throw new InsufficientFundsException("Insufficient funds in source deposit with id " + sourceId);
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
