package com.FinTrace.smartWallet.CustomerService.dao.impl;

import com.FinTrace.smartWallet.CustomerService.dao.DepositDao;
import com.FinTrace.smartWallet.CustomerService.exception.DepositNotFoundException;
import com.FinTrace.smartWallet.CustomerService.model.Deposit;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("memo")
public class DepositInMemoryDao implements DepositDao {

    private final Map<Long, Deposit> deposits = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @Override
    public Deposit save(Deposit deposit) {
        if (deposit.getId() == null) {
            return insert(deposit);
        } else {
            return update(deposit);
        }
    }

    private Deposit insert(Deposit deposit) {
        if (deposit.getId() == null) {
            long id = currentId.incrementAndGet();
            deposit.setId(id);
            deposit.setVersion(0L); // Initialize version to 0 for new deposits
        }
        deposits.put(deposit.getId(), deposit);
        return deepCopy(deposit);
    }

    private Deposit update(Deposit deposit) {
        Deposit existingDeposit = deposits.get(deposit.getId());
        if (existingDeposit == null) {
            throw new DepositNotFoundException("Deposit with id " + deposit.getId() + " does not exist.");
        }
        if (!existingDeposit.getVersion().equals(deposit.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(
                    "Deposit with id " + deposit.getId() + " was updated by another transaction.",
                    Deposit.class);
        }
        deposit.setVersion(deposit.getVersion() + 1); // Increment version after successful update
        deposits.put(deposit.getId(), deposit);
        return deepCopy(deposit);
    }

    @Override
    public Optional<Deposit> findById(Long id) {
        Deposit deposit = deposits.get(id);
        if (deposit == null) return Optional.empty();
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            Deposit snapshot = deepCopy(deposit);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) {
                        deposits.put(id, snapshot);
                    }
                }
            });
        }
        return Optional.of(deposit);
    }

    private Deposit deepCopy(Deposit deposit) {
        // Implement a deep copy logic if necessary, or return the original if immutability is not a concern
        Deposit copy = new Deposit();
        copy.setId(deposit.getId());
        copy.setAmount(deposit.getAmount());
        copy.setCustomer(deposit.getCustomer()); // Assuming Customer is immutable or has its own deep copy logic
        copy.setVersion(deposit.getVersion());
        return copy;
    }

    @Override
    public void deleteById(Long id) {
        if (deposits.containsKey(id)) {
            deposits.remove(id);
        } else {
            throw new DepositNotFoundException("Deposit with id " + id + " does not exist.");
        }
    }

    @Override
    public List<Deposit> findByCustomerId(Long id) {
        return deposits.values().stream()
                .filter(deposit -> deposit.getCustomer() != null && deposit.getCustomer().getId().equals(id))
                .map(this::deepCopy)
                .toList();
    }

    @Override
    public List<Deposit> findAll() {
        return deposits.values().stream()
                .map(this::deepCopy)
                .toList();
    }

    @Override
    public void flush() {
    }
}
