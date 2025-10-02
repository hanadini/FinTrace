package com.FinTrace.customerSystem.integration;

import com.FinTrace.customerSystem.dto.CustomerDto;
import com.FinTrace.customerSystem.dto.FileType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "customerClient",
        url = "http://localhost:8080/api/customers",
        contextId = "customerClient"
        //configuration = CustomerClientConfig.class
)
public interface CustomerClient {

    @GetMapping
    List<CustomerDto> getAllCustomers();

    @GetMapping("/name/{name}")
    List<CustomerDto> getCustomersByName(@PathVariable("name") String name);

    @GetMapping("/{id}")
    CustomerDto getCustomerById(@PathVariable("id") Long id);

    @PostMapping
    CustomerDto addCustomer(@RequestBody CustomerDto customer);

    @PutMapping("/{id}")
    CustomerDto updateCustomer(@PathVariable("id") Long id,
                               @RequestBody CustomerDto updatedCustomer);

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteCustomer(@PathVariable("id") Long id);

    @GetMapping(value = "/export")
    byte[] exportCustomers(@RequestParam("fileType") FileType fileType);

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> importCustomers(@RequestPart("file") MultipartFile file,
                                           @RequestParam("fileType") FileType fileType) ;
}