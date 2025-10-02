package com.FinTrace.customerSystem.dao;

import com.FinTrace.customerSystem.model.Deposit;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DepositDao {
    Deposit save(Deposit deposit);

    Optional<Deposit> findById(Long id);

    void deleteById(Long id);

    List<Deposit> findByCustomerId(Long id);

    List<Deposit> findAll();

    void flush();
}
