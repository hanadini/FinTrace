package com.FinTrace.smartWallet.CustomerService.service;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.CustomerType;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    @Autowired
    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @PostConstruct
    public void init() {
        // Initialize some sample customers
        addCustomer(RealCustomer.builder()
                .id(null)
                .name("Alice")
                .phoneNumber("5551234567")
                .email("ed@gmail.com")
                .type(CustomerType.REAL)
                .family("Johnson")
                .build());
    }

    public Customer addCustomer(Customer customer) {
        return customerDao.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        if (customerDao.existsById(id)) {
            return customerDao.update(id, updatedCustomer);
        }
        return null; // or throw an exception
    }

    public boolean deleteCustomer(Long id) {
        if (customerDao.existsById(id)) {
            customerDao.delete(id);
            return true;
        }
        return false; // or throw an exception
    }

    public Customer getCustomerById(Long id) {
        return customerDao.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }
}
