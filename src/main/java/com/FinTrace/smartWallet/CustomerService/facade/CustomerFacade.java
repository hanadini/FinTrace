package com.FinTrace.smartWallet.CustomerService.facade;

import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.mapper.CustomerMapper;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public boolean deleteCustomer(Long id) {
        return customerService.deleteCustomer(id);
    }

    public CustomerDto getCustomerById(Long id) {
        Customer customerById = customerService.getCustomerById(id);
        return customerById != null ? mapper.toDto(customerById) : null;
    }

    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}