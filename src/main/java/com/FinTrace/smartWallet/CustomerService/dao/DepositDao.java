package com.FinTrace.smartWallet.CustomerService.dao;

import com.FinTrace.smartWallet.CustomerService.model.Deposit;

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
