package com.FinTrace.smartWallet.CustomerService.controller;

import com.FinTrace.smartWallet.CustomerService.dto.FileType;
import com.FinTrace.smartWallet.CustomerService.dto.LegalCustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.RealCustomerDto;
import com.FinTrace.smartWallet.CustomerService.facade.CustomerFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(customerFacade.getAllCustomers());
    }

    @Operation(summary = "Get customers by name", description = "Retrieve a list of customers by their name")
    @GetMapping("/name/{name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers found",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            oneOf = {
                                                    RealCustomerDto.class,
                                                    LegalCustomerDto.class
                                            }
                                    )
                            )
                    )),
            @ApiResponse(responseCode = "404",
                    description = "No customers found with the given name", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
            ))
    })
    public ResponseEntity<?> getCustomersByName(@PathVariable String name) {
        List<CustomerDto> customers = customerFacade.getCustomersByName(name);
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by its unique identifier")
    @GetMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    oneOf = {
                                            RealCustomerDto.class,
                                            LegalCustomerDto.class
                                    }
                            )
                    )),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public ResponseEntity<?> getCustomerById(@PathVariable @Positive(message = "id should be positive") Long id) {
        CustomerDto customerDto = customerFacade.getCustomerById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(customerDto);
    }

    @Operation(summary = "Add a new customer", description = "Create a new customer")
    @PostMapping
    public ResponseEntity<CustomerDto> addCustomer(
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
                                                      "phoneNumber": "+1234567890",
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
                                                        "phoneNumber": "+9876543210",
                                                        "type": "LEGAL",
                                                        "legalAddress": "Street 123, KM, TR"
                                                    }                          
                                                    """
                                    )
                            }
                    )

            )
            @Valid @RequestBody CustomerDto customer
    ) {
        return ResponseEntity.ok(customerFacade.addCustomer(customer));
    }

    @Operation(summary = "Update a customer", description = "Update an existing customer")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id,
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
        return ResponseEntity.ok(customerFacade.updateCustomer(id, updatedCustomer));
    }

    @Operation(summary = "Delete a customer", description = "Remove a customer from the customer system")
    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    ))
    })
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        customerFacade.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Customer deleted successfully");
    }

    @Operation(summary = "Export customers", description = "Export all customers to a file")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCustomers(FileType fileType) throws IOException {
        byte[] fileContent = customerFacade.exportCustomers(fileType );
        String name;
        if(FileType.BINARY.equals((fileType))){
            name = "customers.dat";
        } else {
            name = "customers.json";
        }
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=customers.dat")
                .contentType( MediaType.APPLICATION_OCTET_STREAM)
                .body(fileContent);
    }

    @Operation(summary = "Import customers", description = "Import customers from a file")
    @PostMapping(value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importCustomers(@RequestParam("file") MultipartFile file, @RequestParam("fileType") FileType fileType) throws IOException {
        byte[] fileContent = file.getBytes();
        customerFacade.importCustomers(fileContent, fileType);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Customers imported successfully");
    }

}
