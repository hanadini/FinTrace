package com.FinTrace.smartWallet.CustomerService.dao.impl;
import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        if(!existsById(customer.getId())) {
            Long id = currentId.incrementAndGet();
            customer.setId(id);
        }
        customers.put(customer.getId(), customer);
        return customer;
    }

    public void deleteById(Long id) {
        customers.remove(id);
    }

    public Optional<Customer> findById(Long id) {
        if (!existsById(id)) {
            return Optional.empty();
        }
        return Optional.of(customers.get(id));
    }

    public List<Customer> findAll() {
        return customers.values().stream().toList();
    }

    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return customers.containsKey(id);
    }

    @Override
    public List<Customer> findByNameIgnoreCase(String name) {
        return customers.values().stream()
                .filter(c -> c.getName() != null && c.getName().equalsIgnoreCase(name))
                .toList();
    }
}