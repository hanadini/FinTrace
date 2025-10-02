package com.FinTrace.customerSystem.lifeCycle;

import com.FinTrace.customerSystem.dto.FileType;
import com.FinTrace.customerSystem.exception.DuplicateCustomerException;
import com.FinTrace.customerSystem.facade.CustomerFacade;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

@Component
@Profile("memo")
@ConditionalOnProperty(
        prefix = "memo.lifecycle",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class DataPersistenceManager {

    private final CustomerFacade customerFacade;

    private final FileType fileType = FileType.JSON;

    @Value("${memo.fileName.customer}")
    private String customerFileName;

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
        // Handle any cleanup or data persistence before the application shuts down
        System.out.println("DataPersistenceManager: Cleaning up data...");
        exportCustomersToFile();
    }

    private void exportCustomersToFile() {
        try {
            byte[] fileContent = customerFacade.exportCustomers(fileType);
            writeToFile(fileContent, customerFileName);
        } catch (IOException e) {
            System.err.println("Error exporting customers: " + e.getMessage());
        }
    }

    private void writeToFile(byte[] fileContent, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(fileContent);
            System.out.println("Customers exported successfully");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());

        }
    }

    private void importCustomersFromFile() {
        try {
            byte[] fileContent = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(customerFileName));
            customerFacade.importCustomers(fileContent, fileType);
            System.out.println("Customers imported successfully");
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        } catch (DuplicateCustomerException e) {
            System.err.println("Error importing customers: " + e.getMessage());
        }
    }
}