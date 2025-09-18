package com.FinTrace.smartWallet.CustomerService.dao;

import com.FinTrace.smartWallet.CustomerService.model.Customer;

import java.util.List;

public interface CustomerDao {
    Customer save(Customer customer);
    Customer findById(Long id);
    boolean existsById(Long id);
    Customer update(Long id, Customer customer);
    boolean delete(Long id);
    List<Customer> findAll();
}
