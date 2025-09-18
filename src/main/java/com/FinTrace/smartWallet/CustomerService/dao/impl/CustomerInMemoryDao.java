package com.FinTrace.smartWallet.CustomerService.dao.impl;
import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CustomerInMemoryDao implements CustomerDao {

    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @PostConstruct
    public void init() {
    }

    public Customer save(Customer customer) {
        long id = currentId.incrementAndGet();
        customer.setId(id);
        customers.put(id, customer);
        return customer;
    }

    public Customer update(Long id, Customer customer) {
        customer.setId(id);
        customers.put(id, customer);
        return customer;
    }

    public boolean delete(Long id) {
        return customers.remove(id) != null;
    }

    public Customer findById(Long id) {
        return customers.get(id);
    }

    public List<Customer> findAll() {
        return customers.values().stream().toList();
    }

    public boolean existsById(Long id) {
        return customers.containsKey(id);
    }
}