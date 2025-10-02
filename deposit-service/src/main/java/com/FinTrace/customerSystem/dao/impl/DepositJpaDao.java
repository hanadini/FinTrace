package com.FinTrace.customerSystem.dao.impl;

import com.FinTrace.customerSystem.dao.DepositDao;
import com.FinTrace.customerSystem.model.Deposit;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("jpa")
public interface DepositJpaDao extends JpaRepository<Deposit, Long>, DepositDao {
}