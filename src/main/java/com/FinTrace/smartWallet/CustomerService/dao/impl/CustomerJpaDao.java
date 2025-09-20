package com.FinTrace.smartWallet.CustomerService.dao.impl;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Primary
@Profile("jpa")
public interface CustomerJpaDao extends JpaRepository<Customer, Long>, CustomerDao {

}
