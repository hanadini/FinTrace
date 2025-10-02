package com.FinTrace.customerSystem.dao.impl;
import com.FinTrace.customerSystem.dao.CustomerDao;
import com.FinTrace.customerSystem.model.Customer;
import com.FinTrace.customerSystem.model.LegalCustomer;
import com.FinTrace.customerSystem.model.RealCustomer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("memo")
public class CustomerInMemoryDao implements CustomerDao {

    private final Map<Long, Customer> customers = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);
    private final Validator validator;

    @Autowired
    public CustomerInMemoryDao(Validator validator) {
        this.validator = validator;
    }

    public Customer save(Customer customer) {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        if(!existsById(customer.getId())){
            long id = currentId.incrementAndGet();
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
                .filter(customer -> customer.getName() != null &&
                        customer.getName().equalsIgnoreCase(name))
                .toList();
    }

    @Override
    public boolean realCustomerExists(String name, String family) {
        return customers.values().stream()
                .anyMatch(customer -> customer instanceof RealCustomer realCustomer &&
                        customer.getName() != null && customer.getName().equalsIgnoreCase(name) &&
                        realCustomer.getFamily() != null && realCustomer.getFamily().equalsIgnoreCase(family));
    }

    @Override
    public boolean legalCustomerExists(String name) {
        return customers.values().stream()
                .anyMatch(customer -> customer instanceof LegalCustomer legalCustomer &&
                        customer.getName() != null && customer.getName().equalsIgnoreCase(name));
    }
}