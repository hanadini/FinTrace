package com.FinTrace.smartWallet.facade;

import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.FileType;
import com.FinTrace.smartWallet.CustomerService.dto.RealCustomerDto;
import com.FinTrace.smartWallet.CustomerService.facade.CustomerFacade;
import com.FinTrace.smartWallet.CustomerService.model.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("memo")
public class CustomerFacadeTest {

    @TempDir
    Path tempDir;

    @Autowired
    private CustomerFacade customerFacade;

    @BeforeEach
    void cleanUp() {
        customerFacade.getAllCustomers().forEach(customer -> {
            customerFacade.deleteCustomer(customer.getId());
        });
    }

    @Test
    void testImportBinaryAndVerifyCustomers() throws IOException {
        RealCustomerDto realCustomerDtoA = RealCustomerDto.builder()
                .name("Alice")
                .family("Smith")
                .phoneNumber("1234567890")
                .build();
        realCustomerDtoA.setType(CustomerType.REAL);
        RealCustomerDto realCustomerDtoB = RealCustomerDto.builder()
                .name("Bob")
                .family("Johnson")
                .phoneNumber("0987654321")
                .build();
        realCustomerDtoB.setType(CustomerType.REAL);
        List<RealCustomerDto> customers = Arrays.asList(
                realCustomerDtoA, realCustomerDtoB);
        File binaryFile = tempDir.resolve("customers.dat").toFile();
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binaryFile))) {
            oos.writeObject(customers);
        }

        byte[] fileContent = java.nio.file.Files.readAllBytes(binaryFile.toPath());
        customerFacade.importCustomers(fileContent, FileType.BINARY);

        List<CustomerDto> allCustomers = customerFacade.getAllCustomers();
        assert allCustomers.size() == 2 : "Expected 2 customers, found: " + allCustomers.size();
        assertTrue(allCustomers.stream()
                .anyMatch(c -> c.getName().equals("Alice") &&
                        ((RealCustomerDto)c).getFamily().equals("Smith") &&
                        c.getPhoneNumber().equals("1234567890")));
        assertTrue(allCustomers.stream()
                .anyMatch(c -> c.getName().equals("Bob") &&
                        ((RealCustomerDto)c).getFamily().equals("Johnson") &&
                        c.getPhoneNumber().equals("0987654321")));
    }

    @Test
    void testExportBinaryAndVerifyFile() throws IOException, ClassNotFoundException {
        RealCustomerDto realCustomerDto = RealCustomerDto.builder()
                .name("Charlie")
                .family("Brown")
                .phoneNumber("1122334455")
                .build();
        realCustomerDto.setType(CustomerType.REAL);
        customerFacade.addCustomer(realCustomerDto);
        RealCustomerDto realCustomerDto2 = RealCustomerDto.builder()
                .name("David")
                .family("Williams")
                .phoneNumber("5566778899")
                .build();
        realCustomerDto2.setType(CustomerType.REAL);
        customerFacade.addCustomer(realCustomerDto2);

        byte[] exportedData = customerFacade.exportCustomers(FileType.BINARY);

        assertNotNull(exportedData, "Exported data should not be null");

        try(ObjectInputStream ois = new ObjectInputStream(new java.io.ByteArrayInputStream(exportedData))) {
            List<RealCustomerDto> importedCustomers = (List<RealCustomerDto>) ois.readObject();
            assertNotNull(importedCustomers, "Imported customers should not be null");
            assertTrue(importedCustomers.size() >= 2, "Expected at least 2 customers, found: " + importedCustomers.size());
            assertTrue(importedCustomers.stream()
                    .anyMatch(c -> c.getName().equals("Charlie") &&
                            c.getFamily().equals("Brown") &&
                            c.getPhoneNumber().equals("1122334455")));
            assertTrue(importedCustomers.stream()
                    .anyMatch(c -> c.getName().equals("David") &&
                            c.getFamily().equals("Williams") &&
                            c.getPhoneNumber().equals("5566778899")));
        }
    }

}
