package com.FinTrace.smartWallet.CustomerService.lifeCycle;

import com.FinTrace.smartWallet.CustomerService.dto.FileType;
import com.FinTrace.smartWallet.CustomerService.exception.DuplicateCustomerException;
import com.FinTrace.smartWallet.CustomerService.facade.CustomerFacade;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Component
@Profile("memory")
public class DataPersistenceManager {
    private final CustomerFacade customerFacade;
    private final FileType fileType = FileType.JSON;

    @Value("${memo.fileName}")
    private String fileName;

    @Autowired
    public DataPersistenceManager(CustomerFacade customerFacade) {
        this.customerFacade = customerFacade;
    }

    @PostConstruct
    public void onStartup() {
        // Initialize or load data from the database
        // This could be loading initial customers or any other setup needed
        System.out.println("DataPersistenceManager: Initializing data...");
        importCustomersFromFile();
    }

    @PreDestroy
    public void onShutdown() {
        // Save data to the database or perform any cleanup
        System.out.println("DataPersistenceManager: Saving data before shutdown...");
        exportCustomersToFile();
    }

    private void exportCustomersToFile() {
        try {
            byte[] fileContent = customerFacade.exportCustomers(fileType);
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(fileContent);
                System.out.println("Customers exported successfully");
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());

            }
        } catch (IOException e) {
            System.err.println("Error exporting customers: " + e.getMessage());
        }
    }

    private void importCustomersFromFile() {
        try {
            byte[] fileContent = Files.readAllBytes(java.nio.file.Paths.get(fileName));
            customerFacade.importCustomers(fileContent, fileType);
            System.out.println("Customers imported successfully");
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        } catch (DuplicateCustomerException e) {
            System.err.println("Error importing customers: " + e.getMessage());
        }
    }
}
