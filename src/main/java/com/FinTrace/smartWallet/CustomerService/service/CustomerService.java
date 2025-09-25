package com.FinTrace.smartWallet.CustomerService.service;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.exception.CustomerNotFoundException;
import com.FinTrace.smartWallet.CustomerService.exception.DuplicateCustomerException;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.LegalCustomer;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    @Autowired
    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public Customer addCustomer(Customer customer) {
        if (customer instanceof RealCustomer &&
                customerDao.realCustomerExists(customer.getName(), ((RealCustomer) customer).getFamily())) {
            throw new DuplicateCustomerException("Customer with name " + customer.getName() + " and family " + ((RealCustomer) customer).getFamily() + " already exists.");
        } else if (customer instanceof LegalCustomer &&
                customerDao.legalCustomerExists(customer.getName())) {
            throw new DuplicateCustomerException("Customer with name " + customer.getName() + " already exists.");
        }
        return customerDao.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        if (customerDao.existsById(id)) {
            updatedCustomer.setId(id); // Ensure the ID is set for the update
            return customerDao.save(updatedCustomer);
        }
        return null; // or throw an exception
    }

    public void deleteCustomer(Long id) {
        if (customerDao.existsById(id)) {
            customerDao.deleteById(id);
        } else {
            throw new CustomerNotFoundException("Customer not found with id: " + id);
        }
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerDao.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    public List<Customer> findByName(String name) {
        List<Customer> byNameIgnoreCase = customerDao.findByNameIgnoreCase(name);
        if (byNameIgnoreCase.isEmpty()) {
            throw new CustomerNotFoundException("Customer not found with name: " + name);
        }
        return byNameIgnoreCase;
    }

}
