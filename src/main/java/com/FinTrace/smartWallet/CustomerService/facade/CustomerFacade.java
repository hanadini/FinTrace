package com.FinTrace.smartWallet.CustomerService.facade;

import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.FileType;
import com.FinTrace.smartWallet.CustomerService.exception.CustomerNotFoundException;
import com.FinTrace.smartWallet.CustomerService.mapper.CustomerMapper;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class CustomerFacade {

    private final CustomerMapper mapper;
    private final CustomerService customerService;

    @Autowired
    public CustomerFacade(CustomerMapper mapper, CustomerService customerService) {
        this.mapper = mapper;
        this.customerService = customerService;
    }

    public CustomerDto addCustomer(CustomerDto customer) {
        Customer entity = mapper.toEntity(customer);
        entity = customerService.addCustomer(entity);
        return mapper.toDto(entity);
    }

    public CustomerDto updateCustomer(Long id, CustomerDto updatedCustomer) {
        Customer entity = mapper.toEntity(updatedCustomer);
        entity = customerService.updateCustomer(id, entity);
        return entity != null ? mapper.toDto(entity) : null;
    }

    public void deleteCustomer(Long id) {
        customerService.deleteCustomer(id);
    }

    public CustomerDto getCustomerById(Long id) {
        Optional<Customer> customerById = customerService.getCustomerById(id);
        return customerById.map(mapper::toDto)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + id));
    }

    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<CustomerDto> getCustomersByName(String name) {
        return customerService.findByName(name)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public byte[] exportCustomers(FileType fileType) throws IOException {
        List<CustomerDto> customers = getAllCustomers();
        if(FileType.BINARY.equals(fileType)) {
            return mapper.toBytes(customers);
        } else {
            return mapper.toJsonBytes(customers);
        }
    }

    public void importCustomers(byte[] fileContent, FileType fileType) {
        List<CustomerDto> customers;
        if(FileType.BINARY.equals(fileType)) {
            customers = mapper.byteToDtos(fileContent);
        } else {
            customers = mapper.jsonToDtos(fileContent);
        }
        for (CustomerDto customer : customers) {
            try{
                addCustomer(customer);
            } catch (Exception e) {
                System.err.println("Failed to add customer: " + customer + " due to " + e.getMessage());
            }
        }
    }
}