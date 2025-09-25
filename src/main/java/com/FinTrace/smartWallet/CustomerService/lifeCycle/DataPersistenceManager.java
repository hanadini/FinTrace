package com.FinTrace.smartWallet.CustomerService.lifeCycle;

import com.FinTrace.smartWallet.CustomerService.dto.FileType;
import com.FinTrace.smartWallet.CustomerService.exception.DuplicateCustomerException;
import com.FinTrace.smartWallet.CustomerService.facade.CustomerFacade;
import com.FinTrace.smartWallet.CustomerService.facade.DepositFacade;
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
    private final DepositFacade depositFacade;

    private final FileType fileType = FileType.JSON;

    @Value("${memo.fileName.customer}")
    private String customerFileName;

    @Value("${memo.fileName.deposit}")
    private String depositFileName;


    @Autowired
    public DataPersistenceManager(CustomerFacade customerFacade, DepositFacade depositFacade) {
        this.customerFacade = customerFacade;
        this.depositFacade = depositFacade;
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
            writeToFile(fileContent, customerFileName);
            byte[] depositFileContent = depositFacade.exportDeposits(fileType);
            writeToFile(depositFileContent, depositFileName);
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
            byte[] depositFileContent = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(depositFileName));
            depositFacade.importDeposits(depositFileContent, fileType);
            System.out.println("Customers imported successfully");
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        } catch (DuplicateCustomerException e) {
            System.err.println("Error importing customers: " + e.getMessage());
        }
    }
}
