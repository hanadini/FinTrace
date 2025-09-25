package com.FinTrace.smartWallet.CustomerService.dao.impl;

import com.FinTrace.smartWallet.CustomerService.dao.DepositDao;
import com.FinTrace.smartWallet.CustomerService.model.Deposit;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("jpa")
public interface DepositJpaDao extends JpaRepository<Deposit, Long>, DepositDao {
}
