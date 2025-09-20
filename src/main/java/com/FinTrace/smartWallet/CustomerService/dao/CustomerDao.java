package com.FinTrace.smartWallet.CustomerService.dao;

import com.FinTrace.smartWallet.CustomerService.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    Customer save(Customer customer);
    void deleteById(Long id);
    Optional<Customer> findById(Long id);
    List<Customer> findAll();
    boolean existsById(Long id);
    List<Customer> findByNameIgnoreCase(String name);
}
