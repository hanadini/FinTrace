package com.FinTrace.smartWallet.CustomerService.controller;

import com.FinTrace.smartWallet.CustomerService.dto.LegalCustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.RealCustomerDto;
import com.FinTrace.smartWallet.CustomerService.facade.CustomerFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerFacade customerFacade;

    @Autowired
    public CustomerController(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    // This is a simple controller class that handles HTTP requests

    @Operation(summary = "Get all customers", description = "Retrieve a list of all customers")
    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        return customerFacade.getAllCustomers();
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by its unique identifier")
    @GetMapping("/{id}")
    public CustomerDto getCustomerById(@PathVariable Long id) {
        return customerFacade.getCustomerById(id);
    }

    @Operation(summary = "Add a new customer", description = "Create a new customer")
    @PostMapping
    public CustomerDto addCustomer(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Contact object to be added",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    oneOf = {
                                            RealCustomerDto.class,
                                            LegalCustomerDto.class
                                    }
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Real Customer Example",
                                            value = """
                                                    {
                                                      "name": "Ryan",
                                                      "email": "Ryn@gmail.com",
                                                      "phoneNumber": "1234567890",
                                                      "type": "PERSONAL",
                                                      "family": "Azt"    
                                                    }                          
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Legal Customer Example",
                                            value = """
                                                    {
                                                      "name": "SFCorp",
                                                      "email": "SFC@gmail.com",
                                                        "phoneNumber": "9876543210",
                                                        "type": "LEGAL",
                                                        "legalAddress": "Street 123, KM, TR"
                                                    }                          
                                                    """
                                    )
                            }
                    )

            )
            @RequestBody CustomerDto customer
    ) {
        return customerFacade.addCustomer(customer);
    }

    @Operation (summary = "Update a customer", description = "Update an existing customer")
    @PutMapping("/{id}")
    public CustomerDto updateCustomer(@PathVariable Long id,
                                      @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                             description = "Updated customer object",
                                             required = true,
                                             content = @Content(
                                                     mediaType = "application/json",
                                                     schema = @Schema(
                                                             oneOf = {
                                                                     RealCustomerDto.class,
                                                                     LegalCustomerDto.class
                                                             }
                                                     ),
                                                     examples = {
                                                             @ExampleObject(
                                                                     name = "Real Customer Example",
                                                                     value = "{"
                                                                             + "\"name\": \"John Doe\","
                                                                             + "\"phoneNumber\": \"+1234567890\","
                                                                             + "\"type\": \"REAL\","
                                                                             + "\"family\": \"Doe\""
                                                                             + "\"email\": \"ed@gamil.com\""
                                                                             + "}"
                                                             ),
                                                             @ExampleObject(
                                                                     name = "Legal Customer Example",
                                                                     value = "{"
                                                                             + "\"company name\": \"sfc\","
                                                                             + "\"phoneNumber\": \"+1234567890\","
                                                                             + "\"type\": \"LEGAL\","
                                                                             + "\"email\": \"sfcorp@gamil.com\""
                                                                             + "\"legalAddress\": \"Street 123, KM, TR\""
                                                                             + "}"
                                                             )
                                                     }
                                             )
                                     )
                                     @RequestBody CustomerDto updatedCustomer
    ) {
        return customerFacade.updateCustomer(id, updatedCustomer);
    }

    @Operation(summary = "Delete a customer", description = "Remove a customer from the customer system")
    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerFacade.deleteCustomer(id);
        if (deleted) {
            return "Customer deleted successfully";
        } else {
            return "Customer not found";
        }
    }
}
