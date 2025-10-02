package com.FinTrace.customerSystem.facade;

import com.FinTrace.customerSystem.dto.CustomerDto;
import com.FinTrace.customerSystem.dto.FileType;
import com.FinTrace.customerSystem.dto.RealCustomerDto;
import com.FinTrace.customerSystem.exception.CustomerNotFoundException;
import com.FinTrace.customerSystem.integration.CustomerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class CustomerFacade {

    private final CustomerClient customerClient;

    @Autowired
    public CustomerFacade(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    public CustomerDto addCustomer(CustomerDto customer) {
        return customerClient.addCustomer(customer);
    }

    public CustomerDto updateCustomer(Long id, CustomerDto updatedCustomer) {
        return customerClient.updateCustomer(id, updatedCustomer);
    }

    public void deleteCustomer(Long id) {
        customerClient.deleteCustomer(id);
    }

    public CustomerDto getCustomerById(Long id) throws CustomerNotFoundException {
        return customerClient.getCustomerById(id);
    }

    public List<CustomerDto> getAllCustomers() {
        return customerClient.getAllCustomers();
    }

    public List<CustomerDto> getCustomersByName(String name) {
        return customerClient.getCustomersByName(name);
    }

    public byte[] exportCustomers(FileType fileType) throws IOException {
        return customerClient.exportCustomers(fileType);
    }

    public void importCustomers(byte[] fileContent, FileType fileType) {
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "customer",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                fileContent
        );

        customerClient.importCustomers(multipartFile, fileType);
    }
}